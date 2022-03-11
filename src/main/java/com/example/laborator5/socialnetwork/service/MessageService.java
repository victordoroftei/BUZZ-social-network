package com.example.laborator5.socialnetwork.service;

import com.example.laborator5.socialnetwork.domain.Message;
import com.example.laborator5.socialnetwork.domain.validators.MessageValidator;
import com.example.laborator5.socialnetwork.repository.paging.Page;
import com.example.laborator5.socialnetwork.repository.paging.Pageable;
import com.example.laborator5.socialnetwork.repository.paging.PageableImplementation;
import com.example.laborator5.socialnetwork.repository.paging.PagingRepository;
import com.example.laborator5.socialnetwork.utils.observer.Observable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;
import com.example.laborator5.socialnetwork.service.dto.MessageDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MessageService implements Observable {

    /**
     * This field retains the observers of the message service
     */
    private List<Observer> observers = new ArrayList<>();

    /**
     * Message repository.
     */
    private PagingRepository<Long, Message> repo;

    /**
     * Message validator.
     */
    private MessageValidator validator;

    /**
     * The number of the current page
     */
    private int page;

    /**
     * Constructor for Message Service class.
     *
     * @param repo      the message repository
     * @param validator the message validator
     */
    public MessageService(PagingRepository<Long, Message> repo, MessageValidator validator) {

        this.repo = repo;
        this.validator = validator;
    }

    /**
     * @return all the messages
     */
    public Iterable<Message> getAll() {

        return this.repo.findAll();
    }

    /**
     * Gets all the messages that a user either sent or received.
     *
     * @param userDTO - the user who requested his messages
     * @return a list of messages, ordered anti-chronologically by date
     */
    public List<Message> getMessagesForAUser(UserDTO userDTO) {

        Long id = userDTO.getId();

        Iterable<Message> messages = this.repo.findAll();

        List<Message> result = new ArrayList<Message>();

        for (Message message : messages) {

            if (message.getFrom().equals(id) || message.getTo().contains(id))

                result.add(message);
        }

        result.sort(Comparator.comparing(Message::getDate).reversed());

        return result;
    }

    /**
     * Gets all the conversations between two users.
     *
     * @param userDTO1 - the first user
     * @param userDTO2 - the second user
     * @return a list of messages
     */
    public List<Message> getConversations(UserDTO userDTO1, UserDTO userDTO2) {

        Long id1 = userDTO1.getId();

        Long id2 = userDTO2.getId();

        Iterable<Message> messages = this.repo.findAll();

        List<Message> result = new ArrayList<Message>();

        for (Message message : messages) {

            if (message.getTo().size() == 1)

                if ((message.getFrom().equals(id1) && message.getTo().get(0).equals(id2)) || (message.getFrom().equals(id2) && message.getTo().get(0).equals(id1)))

                    result.add(message);
        }

        result.sort(Comparator.comparing(Message::getDate).reversed());

        return result;
    }

    /**
     * Creates an id for a new request.
     *
     * @return an id
     */
    private Long setIdForMessage() {

        Long id = 1L;
        while (this.repo.findOne(id) != null)

            id++;

        return id;
    }

    /**
     * Adds a message from a sender to multiple receivers.
     *
     * @param messageDTO - the message we want to add
     */
    public void addMessage(MessageDTO messageDTO) {

        Long from = messageDTO.getFrom().getId();

        List<Long> to = new ArrayList<>();
        messageDTO.getTo().forEach(x -> {
            to.add(x.getId());
        });

        String messageString = messageDTO.getMessage();

        Message message = new Message(this.setIdForMessage(), from, to, messageString, LocalDateTime.now());

        this.validator.validate(message);

        this.repo.save(message);

        this.updateAll();
    }


    /**
     * The role of this method is to reply to a message
     *
     * @param message - the answer of the message
     * @return the message we have sent
     */
    public MessageDTO replyToMessage(MessageDTO message) {

        Long messageID = message.getOriginalMessage();
        Long sender = message.getFrom().getId();

        this.validator.validate(new Message(messageID, sender, Arrays.asList(1L), message.getMessage()));

        List<Long> toList = new ArrayList<>();
        for (UserDTO u : message.getTo()) {

            toList.add(u.getId());
        }

        Message message2 = new Message(this.setIdForMessage(), sender, toList, message.getMessage());
        message2.setOriginalMessage(messageID);

        this.repo.save(message2);

        this.updateAll();

        return new MessageDTO(message2.getId(), null, null, message2.getMessage(), null, null);
    }

    /**
     * The role of this method is to reply to some users
     *
     * @param reply the reply message
     * @return the message we have sent
     */
    public MessageDTO replyToAll(MessageDTO reply) {

        Long from = reply.getFrom().getId();

        this.validator.validate(new Message(1L, from, List.of(1L), reply.getMessage()));

        Message message1 = this.repo.findOne(reply.getOriginalMessage());

        Predicate<Long> predicate = x -> !x.equals(from);

        List<Long> list = message1.getTo().stream().filter(predicate).collect(Collectors.toList());

        if (!message1.getFrom().equals(from))   // this checks if we reply to our own last message
            list.add(message1.getFrom());

        Message message2 = new Message(this.setIdForMessage(), from, list, reply.getMessage());

        message2.setOriginalMessage(reply.getOriginalMessage());

        this.repo.save(message2);

        this.updateAll();

        return new MessageDTO(message2.getId(), null, null, message2.getMessage(), null, null);
    }

    /**
     * This method adds an observer to the message service observers list
     *
     * @param observer - the observer we want to add
     */
    @Override
    public void addObserver(Observer observer) {

        this.observers.add(observer);
    }

    /**
     * This method removes an observer from the observers list
     *
     * @param observer - the observer we want to remove
     */
    @Override
    public void removeObserver(Observer observer) {

        this.observers.remove(observer);
    }

    /**
     * This method notifies all the observers from the list
     */
    @Override
    public void updateAll() {

        this.observers.forEach(Observer::update);
    }

    /**
     * The role of this method is to return messages for a specific user paginated
     *
     * @param userDTO - the user
     * @param page    - the page of the messages we are looking for
     * @param size    - the size of the page
     * @return the page requested by the user
     */
    public List<Message> getMessagesForAUserOnPage(UserDTO userDTO, int page, int size) {

        this.page = page;

        Pageable pageable = new PageableImplementation(page, size);

        Page<Message> messagePage = this.repo.findAll(pageable, this.getMessagesForAUser(userDTO));

        return messagePage.getContent().collect(Collectors.toList());
    }

}
