package com.example.laborator5.socialnetwork.service;

import com.example.laborator5.socialnetwork.domain.Request;
import com.example.laborator5.socialnetwork.domain.validators.Validator;
import com.example.laborator5.socialnetwork.repository.paging.Page;
import com.example.laborator5.socialnetwork.repository.paging.Pageable;
import com.example.laborator5.socialnetwork.repository.paging.PageableImplementation;
import com.example.laborator5.socialnetwork.repository.paging.PagingRepository;
import com.example.laborator5.socialnetwork.utils.observer.Observable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;
import com.example.laborator5.socialnetwork.service.dto.RequestDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is the RequestService class which has the connection to the repo and validator
 */
public class RequestService implements Observable {

    /**
     * This field contains the observers of request service
     */
    private List<Observer> observers = new ArrayList<>();

    /**
     * The repository of the requests
     */
    private PagingRepository<Long, Request> repo;

    /**
     * The validator of the requests
     */
    private Validator<Request> validator;

    /**
     * The index of the current page
     */
    private int page;

    /**
     * The constructor
     *
     * @param repo      - The repository of requests
     * @param validator - The validator of requests
     */
    public RequestService(PagingRepository<Long, Request> repo, Validator<Request> validator) {

        this.repo = repo;
        this.validator = validator;
    }

    /**
     * This method returns all the requests
     *
     * @return iterable which has requests
     */
    public Iterable<Request> getAll() {

        return this.repo.findAll();
    }

    /**
     * This method returns the id of a reguest
     *
     * @param id1 - id of user 1
     * @param id2 - id of user 2
     * @return the id of a request
     */
    public Long fromIDsToID(Long id1, Long id2) {

        for (Request request : this.repo.findAll())

            if ((request.getIdUser1().equals(id1) && request.getIdUser2().equals(id2)) || (request.getIdUser1().equals(id2) && request.getIdUser2().equals(id1)))

                return request.getId();

        return null;
    }

    /**
     * This method returns the first available id for a request
     *
     * @return the first available id
     */
    private Long setIdForRequest() {

        Long id = 1L;
        while (this.repo.findOne(id) != null)

            id++;

        return id;
    }

    /**
     * This method adds a request in our requests db
     *
     * @param requestDTO - the request we want to add
     */
    public void addRequest(RequestDTO requestDTO) {

        Long id1 = requestDTO.getUser1().getId();
        Long id2 = requestDTO.getUser2().getId();

        Request request = new Request(this.setIdForRequest(), id1, id2, "pending");

        this.validator.validate(request);

        Long id = fromIDsToID(id1, id2);

        if (id != null)

            throw new ServiceException("There is already a pending request between these users!\n");

        this.repo.save(request);

        this.updateAll();
    }

    /**
     * This method deletes a request between 2 users
     *
     * @param requestDTO - the request we want to delete
     */
    public void deleteRequest(RequestDTO requestDTO) {

        Long id1 = requestDTO.getUser1().getId();
        Long id2 = requestDTO.getUser2().getId();

        this.validator.validate(new Request(1L, id1, id2, "rejected"));

        Long id = fromIDsToIDOneDirection(id1, id2);

        if (id == null)

            throw new ServiceException("No pending request found between these users!\n");

        this.repo.delete(new Request(id, null, null, null));

        this.updateAll();
    }

    /**
     * This method returns all the requests for a user
     *
     * @param userDTO - the user who is requesting all his requests
     * @return an iterable which contains all user's requests
     */
    public Iterable<Request> getAllRequestsForAUser(UserDTO userDTO) {

        Long id = userDTO.getId();

        List<Request> requestList = new ArrayList<>();

        Iterable<Request> requests = this.repo.findAll();

        List<Request> requests1 = new ArrayList<>();
        requests.forEach(requests1::add);

        Predicate<Request> requestPredicate1 = x -> x.getIdUser1().equals(id);
        Predicate<Request> requestPredicate2 = x -> x.getIdUser2().equals(id);
        Predicate<Request> requestPredicate = requestPredicate1.or(requestPredicate2);

        requests1.stream().filter(requestPredicate).forEach(requestList::add);

        return requestList;
    }

    /**
     * This method returns the request for a user paginated
     *
     * @param user - the user
     * @param page - the index of the request page
     * @param size - the size of the requested page
     * @return the request page of requests
     */
    public Iterable<Request> getAllRequestsForAUserOnPage(UserDTO user, int page, int size) {

        this.page = page;

        Pageable pageable = new PageableImplementation(page, size);

        Page<Request> requestPage = this.repo.findAll(pageable, this.getAllRequestsForAUser(user));

        return requestPage.getContent().collect(Collectors.toList());
    }

    /**
     * This method is for accepting a request between 2 users if it exists
     *
     * @param requestDTO - The request we want to accept
     */
    public void acceptRequest(RequestDTO requestDTO) {

        Long id1 = requestDTO.getUser1().getId();
        Long id2 = requestDTO.getUser2().getId();
        String status = requestDTO.getStatus();

        this.validator.validate(new Request(1L, id1, id2, status));

        Long id = fromIDsToIDOneDirection(id1, id2);

        if (id == null)

            throw new ServiceException("There is no request between these users!\n");

        this.repo.delete(new Request(id, null, null, null));

        this.updateAll();
    }

    /**
     * Method for checking if a request exists only one way.
     *
     * @param id1 id of the first user
     * @param id2 id of the second user
     * @return the id of a friendship if it exists; null otherwise
     */
    private Long fromIDsToIDOneDirection(Long id1, Long id2) {

        for (Request r : this.repo.findAll())

            if (r.getIdUser1().equals(id1) && r.getIdUser2().equals(id2))

                return r.getId();

        return null;
    }

    /**
     * This method adds an observer to our current observers list
     *
     * @param observer - the observer we want to add
     */
    @Override
    public void addObserver(Observer observer) {

        observers.add(observer);
    }

    /**
     * This method removes an observer from our current observers list
     *
     * @param observer - the observer we want to remove
     */
    @Override
    public void removeObserver(Observer observer) {

        observers.remove(observer);
    }

    /**
     * This method updates all the observers of the request service
     */
    @Override
    public void updateAll() {

        observers.forEach(Observer::update);
    }

    /**
     * This method gets a request if exists
     *
     * @param requestDTO - the request we are looking for
     * @return the requests if exists
     * otherwise, false
     */
    public Request getRequest(RequestDTO requestDTO) {

        Long id = this.fromIDsToID(requestDTO.getUser1().getId(), requestDTO.getUser2().getId());

        if (id == null)

            return null;

        else

            return this.repo.findOne(id);
    }

}
