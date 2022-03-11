package com.example.laborator5.socialnetwork.service;

import com.example.laborator5.socialnetwork.domain.Event;
import com.example.laborator5.socialnetwork.domain.User;
import com.example.laborator5.socialnetwork.domain.validators.Validator;
import com.example.laborator5.socialnetwork.repository.database.EventParticipantsDB;
import com.example.laborator5.socialnetwork.repository.paging.Page;
import com.example.laborator5.socialnetwork.repository.paging.Pageable;
import com.example.laborator5.socialnetwork.repository.paging.PageableImplementation;
import com.example.laborator5.socialnetwork.repository.paging.PagingRepository;
import com.example.laborator5.socialnetwork.service.dto.EventDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.example.laborator5.socialnetwork.utils.observer.Observable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class connects the super service with the repos of the events
 */
public class EventService implements Observable {

    /**
     * The list of the observers
     */
    private List<Observer> observers = new ArrayList<>();

    /**
     * The connection to the event repo
     */
    private PagingRepository<Long, Event> eventRepository;

    /**
     * The connection to the event participants repo
     */
    private EventParticipantsDB eventParticipantsRepository;

    /**
     * The validator for the event service
     */
    private Validator<Event> validator;

    /**
     * The index of the current page
     */
    private int page;

    /**
     * This is the constructor of our class.
     *
     * @param eventRepository     the event repo
     * @param eventParticipantsDB the event participant repo
     * @param validator           the validator of the event service object
     */
    public EventService(PagingRepository<Long, Event> eventRepository, EventParticipantsDB eventParticipantsDB, Validator<Event> validator) {

        this.eventRepository = eventRepository;
        this.eventParticipantsRepository = eventParticipantsDB;
        this.validator = validator;
    }

    /**
     * Method for adding an event.
     *
     * @param event the event that will be added
     */
    public void addEvent(EventDTO event) {

        Event e = new Event(null, event.getName(), event.getOrganizer().getId(), event.getDescription(), event.getDate(), null);

        this.validator.validate(e);

        e.setId(this.generateIdForEvent());

        this.eventRepository.save(e);
        this.updateAll();
    }

    /**
     * Method for deleting an event.
     *
     * @param event the event that will be deleted
     */
    public void deleteEvent(EventDTO event) {

        Event e = new Event(event.getId(), null, null, null, null, null);

        this.eventRepository.delete(e);
        this.updateAll();
    }

    /**
     * Method for updating an event. An update to an event will contain a new date, name and / or organizer.
     *
     * @param event the event that will be updated
     */
    public void updateEvent(EventDTO event) {

        Event e = new Event(event.getId(), event.getName(), event.getOrganizer().getId(), event.getDescription(), event.getDate(), null);

        this.validator.validate(e);

        this.eventRepository.update(e);
        this.updateAll();
    }

    /**
     * Method for adding a participant to an event.
     *
     * @param event   the event that the user will attend
     * @param userDTO the participant that will be added
     * @throws ServiceException if the user is already attending the event
     */
    public void addParticipant(EventDTO event, UserDTO userDTO) {

        Event e = this.eventRepository.findOne(event.getId());

        if (e.getParticipants().contains(userDTO.getId()))

            throw new ServiceException("You are already attending the event!");

        e = new Event(event.getId(), null, null, null, null, List.of(userDTO.getId()));

        this.eventParticipantsRepository.save(e);
        this.updateAll();
    }

    /**
     * Method for removing a participant from an event.
     *
     * @param event   the event that the user will no longer attend
     * @param userDTO the participant that will be removed
     */
    public void deleteParticipant(EventDTO event, UserDTO userDTO) {

        Event e = new Event(event.getId(), null, event.getOrganizer().getId(), null, null, List.of(userDTO.getId()));

        if (userDTO.getId().equals(event.getOrganizer().getId()))

            throw new ServiceException("You can not remove yourself from the participants list!\n");

        if (!e.getParticipants().contains(userDTO.getId()))
            throw new ServiceException("You are not attending this event!");

        this.eventParticipantsRepository.delete(e);
        this.updateAll();
    }

    /**
     * Method used for returning a list containing all the events attended by the user.
     *
     * @param user the user for which the list will be returned
     * @return a list containing the events attended by the user
     */
    public List<EventDTO> getAllEventsForUser(UserDTO user) {

        Iterable<Event> eventList = this.eventRepository.findAll();

        List<EventDTO> events = new ArrayList<>();
        Event event;

        for (Event e : eventList) {

            if (e.getParticipants().contains(user.getId())) {

                event = this.eventRepository.findOne(e.getId());

                List<UserDTO> participants = new ArrayList<>();
                for (Long id : e.getParticipants())
                    participants.add(new UserDTO(id, null, null, null));

                EventDTO eventDTO = new EventDTO(event.getId(), event.getName(), new UserDTO(event.getOrganizer(), null, null, null), event.getDescription(), event.getDate(), participants);

                events.add(eventDTO);
            }
        }

        return events;

    }

    /**
     * Method for finding an event by id.
     *
     * @param id the id of the event
     * @return the event with the given id
     */
    public Event findOne(Long id) {

        return this.eventRepository.findOne(id);
    }

    /**
     * Method that returns all the events.
     *
     * @return a list containing all the events
     */
    public List<Event> getAllEvents(int page, int size) {

        Iterable<Event> eventList;

        if (page != -1) {
            this.page = page;

            Pageable pageable = new PageableImplementation(page, size);

            Iterable<Event> it = this.eventRepository.findAll();
            List<Event> e = new ArrayList<>();
            for (Event ev: it)
                e.add(ev);
            
            e.sort(Comparator.comparing(Event::getDate));

            Page<Event> eventPage = this.eventRepository.findAll(pageable, e);

            eventList = eventPage.getContent().collect(Collectors.toList());
        }

        else
            eventList = this.eventRepository.findAll();

        List<Event> events = new ArrayList<>();

        for (Event e : eventList)
            events.add(e);

        return events;
    }

    /**
     * Method for getting all the events that are organized by a given user.
     *
     * @param user the user that organizes the events
     * @return a list containing events
     */
    public List<EventDTO> getAllEventsForOrganizer(UserDTO user) {

        Iterable<Event> eventList = this.eventRepository.findAll();

        List<EventDTO> events = new ArrayList<>();
        Event event;

        for (Event e : eventList) {

            if (e.getOrganizer().equals(user.getId())) {

                event = this.eventRepository.findOne(e.getId());

                List<UserDTO> participants = new ArrayList<>();
                for (Long id : e.getParticipants())
                    participants.add(new UserDTO(id, null, null, null));

                EventDTO eventDTO = new EventDTO(event.getId(), event.getName(), new UserDTO(event.getOrganizer(), null, null, null), event.getDescription(), event.getDate(), participants);

                events.add(eventDTO);
            }
        }

        return events;

    }

    /**
     * Returns the next available id for a new event.
     *
     * @return an id
     */
    private Long generateIdForEvent() {

        Long id = 1L;
        while (this.eventRepository.findOne(id) != null)

            id++;

        return id;
    }

    /**
     * The role of this method is to add an observer to the list
     *
     * @param observer - the observer we want to add
     */
    @Override
    public void addObserver(Observer observer) {

        this.observers.add(observer);
    }

    /**
     * The role of this method is to remove an observer from the list
     *
     * @param observer - the observer we want to remove
     */
    @Override
    public void removeObserver(Observer observer) {

        this.observers.remove(observer);
    }

    /**
     * The role of this method is to notify all the observers
     */
    @Override
    public void updateAll() {

        this.observers.forEach(Observer::update);
    }

    /**
     * The role of this method is to set the notifications for a user
     *
     * @param event  - the event
     * @param status - the new status of the notifications
     */
    public void setNotificationsForUser(EventDTO event, UserDTO user, Boolean status) {

        if (!this.eventRepository.findOne(event.getId()).getParticipants().contains(user.getId()))

            throw new ServiceException("You are not attending this event!\n");

        this.eventParticipantsRepository.setNotificationsForParticipant(new Event(event.getId(), event.getName(), event.getOrganizer().getId(), event.getDescription(), event.getDate(), List.of(user.getId())), status);

        this.updateAll();
    }

    /**
     * Method that gets the notification status for a participant for a certain event.
     *
     * @param event the event (also contains the participant's id)
     * @param user  the participant
     * @return true if the notifications are enabled; false otherwise
     */
    public boolean getNotificationsForUser(EventDTO event, UserDTO user) {

        if (!this.eventRepository.findOne(event.getId()).getParticipants().contains(user.getId()))

            throw new ServiceException("You are not attending this event!\n");

        return this.eventParticipantsRepository.getNotificationsForParticipant(new Event(event.getId(), event.getName(), event.getOrganizer().getId(), event.getDescription(), event.getDate(), List.of(user.getId())));
    }

    /**
     * The role of this method is to get the upcoming events for a user
     *
     * @param user - the participant
     * @return the list of the events
     */
    public List<Event> getUpcomingEvents(UserDTO user) {

        User u = new User(user.getFirstName(), user.getLastName(), user.getUserName(), null);
        u.setId(user.getId());
        return this.eventParticipantsRepository.getUpcomingEvents(u);
    }

    /**
     * Method that deletes old events.
     */
    public void deleteOldEvents() {

        Iterable<Event> iterable = this.eventRepository.findAll();

        for (Event e: iterable)
            if (e.getDate().compareTo(LocalDateTime.now()) < 0)
                this.eventRepository.delete(e);
    }

}
