package com.example.laborator5.socialnetwork.service;

import com.example.laborator5.socialnetwork.domain.*;
import com.example.laborator5.socialnetwork.domain.validators.ValidationException;
import com.example.laborator5.socialnetwork.network.Graph;
import com.example.laborator5.socialnetwork.repository.paging.Pageable;
import com.example.laborator5.socialnetwork.repository.paging.PageableImplementation;
import com.example.laborator5.socialnetwork.repository.paging.Paginator;
import com.example.laborator5.socialnetwork.service.dto.*;
import com.example.laborator5.socialnetwork.utils.EntitiesOnPage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class that manages the userService and the friendshipService
 */
public class SuperService {

    /**
     * User service
     */
    private UserService userService;

    /**
     * Friendship service
     */
    private FriendshipService friendshipService;

    /**
     * Request service
     */
    private RequestService requestService;

    /**
     * Message service
     */
    private MessageService messageService;

    /**
     * Event service
     */
    private EventService eventService;

    /**
     * Profile service
     */
    private ProfileService profileService;

    /**
     * Post service
     */
    private PostService postService;

    /**
     * This field contains the number of items per page
     */
    private final int size = EntitiesOnPage.getNumber();

    /**
     * Constructor
     *
     * @param userService       - UserService0, user service
     * @param friendshipService - FriendshipService0, friendship service
     */
    public SuperService(UserService userService, FriendshipService friendshipService, RequestService requestService, MessageService messageService, EventService eventService, ProfileService profileService, PostService postService) {

        this.userService = userService;
        this.friendshipService = friendshipService;
        this.requestService = requestService;
        this.messageService = messageService;
        this.eventService = eventService;
        this.profileService = profileService;
        this.postService = postService;

    }

    /**
     * Get all the users
     *
     * @return list of users
     */
    public Iterable<User> getAll() {

        return this.userService.getAll();
    }

    /**
     * Get all friendships
     *
     * @return all the friendships
     */
    public List<FriendshipDTO> getAllFriendships() {

        Iterable<Friendship> friendships = this.friendshipService.getAll();

        List<FriendshipDTO> result = new LinkedList<>();

        for (Friendship friendship : friendships) {

            User user1 = this.userService.findOne(new UserDTO(friendship.getId().getLeft(), "Fn", "Ln", "Un"));
            User user2 = this.userService.findOne(new UserDTO(friendship.getId().getRight(), "Fn", "Ln", "Un"));
            UserDTO dto1 = new UserDTO(user1.getId(), user1.getFirstName(), user1.getLastName(), user1.getUserName());
            UserDTO dto2 = new UserDTO(user2.getId(), user2.getFirstName(), user2.getLastName(), user2.getUserName());
            FriendshipDTO friendshipDTO = new FriendshipDTO(dto1, dto2, friendship.getDate());
            result.add(friendshipDTO);
        }

        return result;
    }

    /**
     * Check if there are 2 users with the usernames given as parameters
     *
     * @param userName1 - String, name of the first username
     * @param userName2 - String, name of the second username
     * @throws ServiceException if one or more users are not found
     */
    private void checkUsersExistenceUsingUsername(String userName1, String userName2) {

        Long id1 = this.userService.fromUserNameToId(userName1);
        Long id2 = this.userService.fromUserNameToId(userName2);

        if (id1 == null && id2 == null)

            throw new ServiceException("No users found with those usernames!\n");

        if (id1 == null)

            throw new ServiceException("No user found with " + userName1 + " as a username!\n");

        if (id2 == null)

            throw new ServiceException("No user found with " + userName2 + " as a username!\n");
    }

    /**
     * Adds a new user
     *
     * @param userRegisterDTO the user that will be added
     */
    public void addUser(UserRegisterDTO userRegisterDTO) {

        User u = new User(userRegisterDTO.getUser().getFirstName(), userRegisterDTO.getUser().getLastName(), userRegisterDTO.getUser().getUserName(), userRegisterDTO.getPassword());
        this.userService.addUser(u);

        User usr = this.userService.findOne(new UserDTO(u.getId(), u.getFirstName(), u.getLastName(), u.getUserName()));

        // When we're adding a new user, we're also adding a blank profile for them.
        ProfileDTO profile = new ProfileDTO(new UserDTO(usr.getId(), usr.getFirstName(), usr.getLastName(), usr.getUserName()), "", "", LocalDate.now(), "");
        this.addProfile(profile, new UserDTO(usr.getId(), usr.getFirstName(), usr.getLastName(), usr.getUserName()));

    }

    /**
     * Adds a new Friendship.
     *
     * @param friendship the friendship that needs to be added
     */
    public void addFriendship(FriendshipDTO friendship) {

        this.userService.validateUsernameOrUsernames(friendship.getUser1().getUserName(), friendship.getUser2().getUserName());
        checkUsersExistenceUsingUsername(friendship.getUser1().getUserName(), friendship.getUser2().getUserName());

        Long id = this.userService.fromUserNameToId(friendship.getUser1().getUserName());
        Long idF = this.userService.fromUserNameToId(friendship.getUser2().getUserName());
        this.friendshipService.addFriendship(new Friendship(id, idF));
    }

    /**
     * Removes a user and the events organized by that user.
     *
     * @param user the user that needs to be removed
     */
    public void removeUser(UserDTO user) {

        List<EventDTO> events = this.eventService.getAllEventsForOrganizer(user);
        for (EventDTO event : events)
            this.eventService.deleteEvent(event);

        events = this.eventService.getAllEventsForUser(user);
        for (EventDTO event : events)
            this.eventService.deleteParticipant(event, user);

        this.userService.deleteUser(user);
    }

    /**
     * Removes a friendship.
     *
     * @param friendship the friendship that needs to be removed
     */
    public void removeFriendship(FriendshipDTO friendship) {

        this.userService.validateUsernameOrUsernames(friendship.getUser1().getUserName(), friendship.getUser2().getUserName());
        checkUsersExistenceUsingUsername(friendship.getUser1().getUserName(), friendship.getUser2().getUserName());
        Long id = this.userService.fromUserNameToId(friendship.getUser1().getUserName());
        Long idF = this.userService.fromUserNameToId(friendship.getUser2().getUserName());

        this.friendshipService.deleteFriendship(new FriendshipDTO(new UserDTO(id, "x", "x", friendship.getUser1().getUserName()), new UserDTO(idF, "x", "x", friendship.getUser2().getUserName()), LocalDateTime.now()));
    }


    /**
     * Method for updating all fields of one user
     *
     * @param user1 the old user
     * @param user2 the new user
     * @return null if the user is updated successfully
     */
    public User updateUser(UserDTO user1, UserDTO user2) {

        return this.userService.updateUser(user1, user2);
    }

    /**
     * Method for updating the date of a friendship
     *
     * @param friendship the old friendship
     * @return null if the friendship is updated successfully
     * @throws ServiceException if the date is not valid
     */
    public Friendship updateFriendship(FriendshipDTO friendship) {

        this.userService.validateUsernameOrUsernames(friendship.getUser1().getUserName(), friendship.getUser2().getUserName());

        checkUsersExistenceUsingUsername(friendship.getUser1().getUserName(), friendship.getUser2().getUserName());

        return this.friendshipService.updateFriendship(friendship);
    }

    /**
     * Get the number of connected components
     *
     * @return the number of connected components
     */
    public int getNumberOfConnectedComponents() {

        Graph graph = new Graph(this.userService.getAll(), this.friendshipService.getAll());

        return graph.connectedComponents().size();
    }

    /**
     * Return the most sociable connection
     *
     * @return the list which contains the users who form the biggest community
     */
    public List<UserDTO> getTheMostSociableConnection() {

        Graph graph = new Graph(this.userService.getAll(), this.friendshipService.getAll());

        List<Long> mostSociable = graph.getTheMostSociableConnection();

        List<UserDTO> result = new ArrayList<>();

        for (Long id : mostSociable) {

            User user = this.userService.findOne(new UserDTO(id, "Fn", "Ln", "Un"));
            result.add(new UserDTO(id, user.getFirstName(), user.getLastName(), user.getUserName()));
        }

        return result;
    }

    /**
     * Return all the communities
     *
     * @return the list of all the communities found in our network
     */
    public List<List<UserDTO>> getAllConnections() {

        Graph graph = new Graph(this.userService.getAll(), this.friendshipService.getAll());

        List<List<Long>> connections = graph.connectedComponents();
        List<List<UserDTO>> result = new ArrayList<>();

        for (List<Long> connection : connections) {

            List<UserDTO> current = new ArrayList<>();

            for (Long id : connection) {

                User user = this.userService.findOne(new UserDTO(id, "Fn", "Ln", "Un"));
                current.add(new UserDTO(id, user.getFirstName(), user.getLastName(), user.getUserName()));
            }

            result.add(current);
        }

        return result;
    }

    /**
     * This method is used for getting all friendships for one user
     *
     * @param userDTO the user
     * @return a list which has all the friendships of a user
     */
    public List<FriendshipDTO> getAllFriendshipsForAUser(UserDTO userDTO) {

        String username = userDTO.getUserName();

        this.userService.validateUsernameOrUsernames(username);

        Long id = this.userService.fromUserNameToId(username);

        if (id == null)

            throw new ServiceException("No username found with the username that you've entered!\n");

        List<FriendshipDTO> friendsList = new ArrayList<>();

        User ourUser = this.userService.findOne(new UserDTO(id, "Fn", "Ln", "Un"));
        UserDTO ourUserDTO = new UserDTO(ourUser.getId(), ourUser.getFirstName(), ourUser.getLastName(), ourUser.getUserName());

        Iterable<Friendship> friendshipIterable = this.friendshipService.getAll();

        List<Friendship> friendshipList = new ArrayList<>();
        friendshipIterable.forEach(friendshipList::add);

        Predicate<Friendship> leftPart = x -> x.getId().getLeft().equals(id);
        Predicate<Friendship> rightPart = x -> x.getId().getRight().equals(id);
        Predicate<Friendship> friendshipPredicate = leftPart.or(rightPart);

        friendshipList.stream().filter(friendshipPredicate).map(x -> {

            User user;

            if (x.getId().getLeft().equals(id))

                user = this.userService.findOne(new UserDTO(x.getId().getRight(), "Fn", "Ln", "Un"));

            else

                user = this.userService.findOne(new UserDTO(x.getId().getLeft(), "Fn", "Ln", "Un"));

            UserDTO friendDTO = new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName());

            return new FriendshipDTO(ourUserDTO, friendDTO, x.getDate());
        }).forEach(friendsList::add);

        return friendsList;

    }

    /**
     * The role of this method is to return friendships for a user paginated
     *
     * @param userDTO - the user
     * @param page    - the number of the requested page
     * @return the requested page of friendships
     */
    public List<FriendshipDTO> getAllFriendshipsForAUserOnPage(UserDTO userDTO, int page) {

        Pageable pageable = new PageableImplementation(page, this.size);

        Paginator<FriendshipDTO> paginator = new Paginator<>(pageable, this.getAllFriendshipsForAUser(userDTO));

        return paginator.paginate().getContent().collect(Collectors.toList());
    }

    /**
     * Method used for getting a user's friendships which were created on a certain month given as a parameter.
     *
     * @param user        the user
     * @param monthString the string containing the name of the month
     * @return a list of the corresponding friendships (stored as FriendshipDTOs)
     */
    public List<FriendshipDTO> getFriendshipsFromMonth(UserDTO user, String monthString) {
        final List<String> possibleMonths = Arrays.asList("january", "february", "march", "april", "may", "june", "july", "august", "September", "october", "november", "december");

        String userName = user.getUserName();
        this.userService.validateUsernameOrUsernames(userName);

        if (!possibleMonths.contains(monthString.toLowerCase()))

            throw new ServiceException("There is no such month!\n");

        List<FriendshipDTO> friendshipDTOs = this.getAllFriendshipsForAUser(new UserDTO(0L, "x", "x", userName));
        List<FriendshipDTO> returnList = new ArrayList<>();

        Predicate<FriendshipDTO> predicate = x -> String.valueOf(x.getDate().getMonth()).equalsIgnoreCase(monthString);
        friendshipDTOs.stream().filter(predicate).forEach(returnList::add);

        if (returnList.size() == 0)

            throw new ServiceException("This user has no friendships on that month!\n");

        return returnList;

    }

    /**
     * Checks if it is possible to add or remove a request between the users with the given usernames.
     *
     * @param userName1 the username of the first user
     * @param userName2 the username of the second user
     * @return a friendship object between the two users with the given usernames, if it is possible to be created
     */
    private Tuple<Long, Long> validateDataForRequests(String userName1, String userName2) {

        this.userService.validateUsernameOrUsernames(userName1, userName2);

        Long id1 = this.userService.fromUserNameToId(userName1);
        if (id1 == null)

            throw new ServiceException("There is no user with " + userName1 + " as an username!\n");

        Long id2 = this.userService.fromUserNameToId(userName2);
        if (id2 == null)

            throw new ServiceException("There is no user with " + userName2 + " as an username!\n");

        return new Tuple<>(id1, id2);
    }

    /**
     * Method for adding a friendship request between two users.
     *
     * @param request the request that must be added
     */
    public void addRequest(RequestDTO request) {

        String userName1 = request.getUser1().getUserName();
        String userName2 = request.getUser2().getUserName();

        Tuple<Long, Long> tuple = this.validateDataForRequests(userName1, userName2);

        try {

            this.friendshipService.friendshipAlreadyExists(new Friendship(tuple.getLeft(), tuple.getRight()));

        } catch (ServiceException ex) {

            throw new ServiceException("Request cannot be added because users are already friends!");
        }

        this.requestService.addRequest(new RequestDTO(new UserDTO(tuple.getLeft(), "Fn", "Ln", "Un"), new UserDTO(tuple.getRight(), "Fn", "Ln", "Un"), LocalDateTime.now(), "pending"));

    }

    /**
     * Method for deleting a request between two users.
     *
     * @param request the request that must be deleted
     */
    public void deleteRequest(RequestDTO request) {

        String userName1 = request.getUser1().getUserName();
        String userName2 = request.getUser2().getUserName();

        Tuple<Long, Long> tuple = this.validateDataForRequests(userName1, userName2);
        try {

            this.friendshipService.friendshipAlreadyExists(new Friendship(tuple.getLeft(), tuple.getRight()));

        } catch (ServiceException ex) {
            throw new ServiceException("There cannot be a request if the users are already friends");
        }

        this.requestService.deleteRequest(new RequestDTO(new UserDTO(tuple.getLeft(), "Fn", "Ln", "Un"), new UserDTO(tuple.getRight(), "Fn", "Ln", "Un"), LocalDateTime.now(), "pending"));

    }

    /**
     * Returns a list of RequestDTOs created from a given iterable of requests.
     *
     * @param requests the iterable of requests
     * @return the list of RequestDTOs
     */
    private List<RequestDTO> fromRequestsToRequestDTOs(Iterable<Request> requests) {

        List<RequestDTO> result = new LinkedList<>();

        for (Request request : requests) {

            User user1 = this.userService.findOne(new UserDTO(request.getIdUser1(), "Fn", "Ln", "Un"));
            User user2 = this.userService.findOne(new UserDTO(request.getIdUser2(), "Fn", "Ln", "Un"));
            UserDTO dto1 = new UserDTO(user1.getId(), user1.getFirstName(), user1.getLastName(), user1.getUserName());
            UserDTO dto2 = new UserDTO(user2.getId(), user2.getFirstName(), user2.getLastName(), user2.getUserName());
            RequestDTO requestDTO = new RequestDTO(dto1, dto2, request.getDate(), request.getStatus().getStatus());
            result.add(requestDTO);
        }

        return result;

    }

    /**
     * Gets all the requests.
     *
     * @return an iterable containing all the requests
     */
    public List<RequestDTO> getAllRequests() {

        Iterable<Request> requests = this.requestService.getAll();

        return this.fromRequestsToRequestDTOs(requests);
    }

    /**
     * Gets all the requests for a user with the given username.
     *
     * @param user the user
     * @return a list of requests for that user
     */
    public List<RequestDTO> getAllRequestsForAUser(UserDTO user, int page) {

        String userName = user.getUserName();
        this.userService.validateUsernameOrUsernames(userName);

        Long id = this.userService.fromUserNameToId(userName);
        if (id == null)

            throw new ServiceException("There is no user with this username!");

        Iterable<Request> requests;

        if (page == -1)

            requests = this.requestService.getAllRequestsForAUser(new UserDTO(id, "Fn", "Ln", "Un"));

        else

            requests = this.requestService.getAllRequestsForAUserOnPage(new UserDTO(id, "Fn", "Ln", "Un"), page, this.size);

        return this.fromRequestsToRequestDTOs(requests);
    }

    /**
     * Accepts a friendship request from the user with the first username to the user with the second username.
     *
     * @param request the request that must be accepted
     */
    public void acceptRequest(RequestDTO request) {

        String userName1 = request.getUser1().getUserName();
        String userName2 = request.getUser2().getUserName();

        this.userService.validateUsernameOrUsernames(userName1, userName2);

        this.checkUsersExistenceUsingUsername(userName1, userName2);

        Long id1 = this.userService.fromUserNameToId(userName1);
        Long id2 = this.userService.fromUserNameToId(userName2);

        this.requestService.acceptRequest(new RequestDTO(new UserDTO(id1, "Fn", "Ln", "Un"), new UserDTO(id2, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending"));

        this.friendshipService.addFriendship(new Friendship(id1, id2));
    }

    /**
     * Rejects a friendship request from the user with the first username to the user with the second username.
     *
     * @param request the request that must be rejected
     */
    public void rejectRequest(RequestDTO request) {

        String userName1 = request.getUser1().getUserName();
        String userName2 = request.getUser2().getUserName();

        this.userService.validateUsernameOrUsernames(userName1, userName2);

        this.checkUsersExistenceUsingUsername(userName1, userName2);

        Long id1 = this.userService.fromUserNameToId(userName1);
        Long id2 = this.userService.fromUserNameToId(userName2);

        this.requestService.deleteRequest(new RequestDTO(new UserDTO(id1, "Fn", "Ln", "Un"), new UserDTO(id2, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending"));

    }

    /**
     * Gets the conversations between two users.
     *
     * @param user1 the first user
     * @param user2 the second user
     * @return a list of messages
     */
    public List<MessageDTO> getConversations(UserDTO user1, UserDTO user2) {

        String userName1 = user1.getUserName();
        String userName2 = user2.getUserName();

        this.userService.validateUsernameOrUsernames(userName1, userName2);
        this.checkUsersExistenceUsingUsername(userName1, userName2);

        Long id1 = this.userService.fromUserNameToId(userName1);
        Long id2 = this.userService.fromUserNameToId(userName2);//id1, id2

        List<Message> messages = this.messageService.getConversations(new UserDTO(id1, "Fn", "Ln", "Un"), new UserDTO(id2, "Fn", "Ln", "Un"));
        List<MessageDTO> list = new ArrayList<>();

        messages.forEach(x -> {
            User u = this.userService.findOne(new UserDTO(x.getFrom(), "Fn", "Ln", "Un"));
            List<Long> ids = x.getTo();

            List<UserDTO> l = new ArrayList<>();

            ids.forEach(y -> {
                User u1 = this.userService.findOne(new UserDTO(y, "Fn", "Ln", "Un"));
                l.add(new UserDTO(u1.getId(), u1.getFirstName(), u1.getLastName(), u1.getUserName()));
            });

            list.add(new MessageDTO(x.getId(), new UserDTO(u.getId(), u.getFirstName(), u.getLastName(), u.getUserName()), l, x.getMessage(), x.getDate(), x.getOriginalMessage()));
        });

        return list;
    }

    /**
     * Adds a message sent from a user to multiple other users.
     *
     * @param message the message
     */
    public void addMessage(MessageDTO message) {

        UserDTO from = message.getFrom();
        List<UserDTO> to = message.getTo();

        try {

            this.userService.validateUsernameOrUsernames(from.getUserName());
        } catch (ValidationException ex) {

            throw new ServiceException("Sender's username cannot be null!\n");
        }

        Long idFrom = this.userService.fromUserNameToId(from.getUserName());
        if (idFrom == null)

            throw new ServiceException("Sender does not exist!\n");

        List<UserDTO> DTOList = new ArrayList<>();
        StringBuilder errorMsgs = new StringBuilder();
        boolean flag;

        for (UserDTO u : to) {

            String username = u.getUserName();

            flag = false;

            if (from.getUserName().equals(username)) {

                errorMsgs.append("Sender's username cannot be the same as receiver's username!\n");
                flag = true;
            }

            try {

                this.userService.validateUsernameOrUsernames(username);
            } catch (ValidationException ex) {

                errorMsgs.append("Receiver's username is invalid!\n");
                flag = true;
            }

            Long id = this.userService.fromUserNameToId(username);
            if (id == null) {

                if (!flag)
                    errorMsgs.append("Receiver's username does not exist!\n");
            } else if (!flag)
                DTOList.add(new UserDTO(id, u.getFirstName(), u.getLastName(), u.getUserName()));

        }

        if (!errorMsgs.toString().equals("") && DTOList.isEmpty())

            throw new ServiceException(errorMsgs.toString());
        //idFrom, idList, messageString

        this.messageService.addMessage(new MessageDTO(message.getId(), new UserDTO(idFrom, null, null, from.getUserName()), DTOList, message.getMessage(), message.getDate(), message.getOriginalMessage()));

        if (!errorMsgs.toString().equals(""))

            throw new ServiceException(errorMsgs.toString());
    }

    /**
     * This method is to reply to a message
     *
     * @param message the reply message
     */
    public MessageDTO replyToMessage(MessageDTO message) {

        return this.messageService.replyToMessage(message);
    }

    /**
     * This method is used for replying to all participants.
     *
     * @param reply the reply to the message
     */
    public MessageDTO replyToAll(MessageDTO reply) {

        return this.messageService.replyToAll(reply);
    }

    /**
     * Get all messages for a user by username
     *
     * @param user the user for which the messages are returned
     * @return a list which contains all the messages that a user has sent or received
     * @throws ServiceException if there is no user with the username given as parameter
     */
    public List<MessageDTO> getAllMessagesForAUser(UserDTO user, int page) {

        String username = user.getUserName();

        this.userService.validateUsernameOrUsernames(username);

        Long id = this.userService.fromUserNameToId(username);

        if (id == null)

            throw new ServiceException("No username found with that username!\n");

        List<Message> messages = this.messageService.getMessagesForAUserOnPage(user, page, this.size);

        List<MessageDTO> list = new ArrayList<>();

        messages.forEach(x -> {
            User u = this.userService.findOne(new UserDTO(x.getFrom(), "Fn", "Ln", "Un"));
            List<Long> ids = x.getTo();

            List<UserDTO> l = new ArrayList<>();

            ids.forEach(y -> {
                User u1 = this.userService.findOne(new UserDTO(y, "Fn", "Ln", "Un"));
                l.add(new UserDTO(u1.getId(), u1.getFirstName(), u1.getLastName(), u1.getUserName()));
            });

            list.add(new MessageDTO(x.getId(), new UserDTO(u.getId(), u.getFirstName(), u.getLastName(), u.getUserName()), l, x.getMessage(), x.getDate(), x.getOriginalMessage()));
        });

        return list;
    }

    /**
     * This method tries to check if a user with the username given as parameter exists
     *
     * @param userRegisterDTO - object which contains the username of the user we want to log in
     * @return the id of the user if he exists
     * @throws ServiceException if there is no user with the entered username
     */
    public Long loginUserUsingUsername(UserRegisterDTO userRegisterDTO) {

        UserDTO userDTO = userRegisterDTO.getUser();

        this.userService.validateUsernameOrUsernames(userDTO.getUserName());

        Long result = this.userService.fromUserNameToId(userDTO.getUserName());

        if (result == null)

            throw new ServiceException("No username found with the entered username!\n");

        if (this.userService.findOne(new UserDTO(result, null, null, null)).getPassword().equals(userRegisterDTO.getPassword()))

            return result;

        else

            throw new ServiceException("Incorrect password!\n");
    }

    /**
     * Returns a filtered list of users.
     *
     * @param list   the list of users that will be filtered
     * @param search the filter string
     * @return a list of users
     */
    public List<UserDTO> filterUsersByName(List<UserDTO> list, String search) {

        String searchLower = search.toLowerCase();
        Predicate<UserDTO> predicate = x -> x.getFirstName().toLowerCase().contains(searchLower) || x.getLastName().toLowerCase().contains(searchLower);

        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Method for getting a map containing all distinct conversations of a user.
     *
     * @param user the user for which the conversations are needed
     * @return a map containing all distinct conversations
     */
    public Map<Integer, List<MessageDTO>> getAllConversations(UserDTO user) {

        Map<Integer, List<MessageDTO>> map = new HashMap<>();

        List<Message> list = this.messageService.getMessagesForAUser(user);

        for (Message m : list) {

            User fromUser = this.userService.findOne(new UserDTO(m.getFrom(), null, null, null));
            UserDTO fromDTO = new UserDTO(fromUser.getId(), fromUser.getFirstName(), fromUser.getLastName(), fromUser.getUserName());

            List<UserDTO> userList = new ArrayList<>();
            List<Long> userIdList = new ArrayList<>();
            userIdList.add(fromUser.getId());

            List<Long> toUsers = new ArrayList<>(m.getTo());
            for (Long id : toUsers) {

                User toUser = this.userService.findOne(new UserDTO(id, null, null, null));
                UserDTO toDTO = new UserDTO(toUser.getId(), toUser.getFirstName(), toUser.getLastName(), toUser.getUserName());

                userList.add(toDTO);
                userIdList.add(id);
            }

            userList.sort(Comparator.comparing(UserDTO::getId));
            Collections.sort(userIdList);

            MessageDTO messageDTO = new MessageDTO(m.getId(), fromDTO, userList, m.getMessage(), m.getDate(), m.getOriginalMessage());

            Integer hashCode = userIdList.hashCode();

            if (map.containsKey(hashCode))
                map.get(hashCode).add(messageDTO);

            else {

                List<MessageDTO> messageDTOList = new ArrayList<>();
                messageDTOList.add(messageDTO);

                map.put(hashCode, messageDTOList);
            }
        }

        return map;
    }

    /**
     * This method creates the page object for a user
     *
     * @param userDTO - the information about the user
     * @return page object for our user
     */
    public Page getPageForUser(UserDTO userDTO) {

        User u = this.userService.findOne(userDTO);
        UserDTO user = new UserDTO(u.getId(), u.getFirstName(), u.getLastName(), u.getUserName(), u.getLastLogin());

        List<FriendshipDTO> friendshipList = this.getAllFriendshipsForAUser(user);

        List<UserDTO> friendsList = friendshipList.stream().map(x -> {
            if (x.getUser1().getId().equals(user.getId()))

                return x.getUser2();

            return x.getUser1();
        }).collect(Collectors.toList());

        List<MessageDTO> messsagesList = this.getAllRecentMessages(user);

        List<RequestDTO> requestsList = this.getAllRecentRequests(user);

        List<EventDTO> eventsList = this.getUpcomingEvents(user);

        return new Page(user, friendsList, messsagesList, requestsList, eventsList);
    }

    /**
     * This method is used for returning reference to the friendship service
     *
     * @return reference to the friendship service
     */
    public FriendshipService getFriendshipService() {

        return this.friendshipService;
    }

    /**
     * This method is used for returning reference to the request service
     *
     * @return reference to the request service
     */
    public RequestService getRequestService() {

        return this.requestService;
    }

    /**
     * This method is used for returning reference to the message service
     *
     * @return reference to the message service
     */
    public MessageService getMessageService() {

        return this.messageService;
    }

    /**
     * Method used for returning the event service of the super service.
     *
     * @return the event service
     */
    public EventService getEventService() {

        return this.eventService;
    }

    /**
     * Method for adding an event.
     *
     * @param event the event that will be added
     */
    public void addEvent(EventDTO event) {

        this.eventService.addEvent(event);
    }

    /**
     * Method for deleting an event.
     *
     * @param event the event that will be deleted
     */
    public void deleteEvent(EventDTO event) {

        this.eventService.deleteEvent(event);
    }

    /**
     * Method for updating an event.
     *
     * @param event the event that will be updated
     */
    public void updateEvent(EventDTO event) {

        this.eventService.updateEvent(event);
    }

    /**
     * Method for adding a participant to an event.
     *
     * @param event the event that the user will attend
     * @param user  the user that will attend the event
     */
    public void addParticipant(EventDTO event, UserDTO user) {

        this.eventService.addParticipant(event, user);
    }

    /**
     * Method for deleting a participant from an event.
     *
     * @param event the event that the user is attending
     * @param user  the user that will no longer attend the event
     */
    public void deleteParticipant(EventDTO event, UserDTO user) {

        this.eventService.deleteParticipant(event, user);
    }

    /**
     * Method for converting events from containing only the IDs of the users to containing all the data of the users.
     *
     * @param list the list of events that will be converted
     * @return a list of converted events
     */
    private List<EventDTO> fromRawEventsToFullEvents(List<EventDTO> list) {

        List<EventDTO> events = new ArrayList<>();

        for (EventDTO e : list) {

            User u = this.userService.findOne(e.getOrganizer());
            UserDTO organizer = new UserDTO(u.getId(), u.getFirstName(), u.getLastName(), u.getUserName());

            List<UserDTO> participants = new ArrayList<>();
            for (UserDTO userDTO : e.getParticipants()) {
                User usr = this.userService.findOne(userDTO);
                UserDTO participant = new UserDTO(usr.getId(), usr.getFirstName(), usr.getLastName(), usr.getUserName());

                participants.add(participant);
            }

            events.add(new EventDTO(e.getId(), e.getName(), organizer, e.getDescription(), e.getDate(), participants));
        }

        return events;
    }

    /**
     * Method that returns all the events.
     *
     * @return a list containing all the events
     */
    public List<EventDTO> getAllEvents(int page) {

        List<Event> list = this.eventService.getAllEvents(page, this.size);

        Event event;

        List<EventDTO> events = new ArrayList<>();

        for (Event e : list) {

            event = this.eventService.findOne(e.getId());

            List<UserDTO> participants = new ArrayList<>();
            for (Long id : e.getParticipants())
                participants.add(new UserDTO(id, null, null, null));

            EventDTO eventDTO = new EventDTO(event.getId(), event.getName(), new UserDTO(event.getOrganizer(), null, null, null), event.getDescription(), event.getDate(), participants);

            events.add(eventDTO);
        }

        return this.fromRawEventsToFullEvents(events);
    }

    /**
     * Method used for returning a list containing all the events attended by the user.
     *
     * @param user the user for which the list will be returned
     * @return a list containing the events attended by the user
     */
    public List<EventDTO> getAllEventsForUser(UserDTO user) {

        List<EventDTO> list = this.eventService.getAllEventsForUser(user);

        return this.fromRawEventsToFullEvents(list);
    }

    /**
     * Method for getting all the events that are organized by a given user.
     *
     * @param user the user that organizes the events
     * @return a list containing events
     */
    public List<EventDTO> getAllEventsForOrganizer(UserDTO user) {

        List<EventDTO> list = this.eventService.getAllEventsForOrganizer(user);

        return this.fromRawEventsToFullEvents(list);
    }

    /**
     * The role of this method is to turn off or on the notifications for a user attending an event
     *
     * @param eventDTO - the event
     * @param userDTO  - the user
     * @param status   - true - if the participants wants to receive notifications
     *                 - false - otherwise
     */
    public void setNotificationsForUser(EventDTO eventDTO, UserDTO userDTO, Boolean status) {

        this.eventService.setNotificationsForUser(eventDTO, userDTO, status);
    }

    /**
     * Method that gets the notification status of an event for a certain user.
     *
     * @param event the event
     * @param user  the participant
     * @return true, if the participant wants to receive notifications; false, otherwise
     */
    public boolean getNotificationsForUser(EventDTO event, UserDTO user) {

        return this.eventService.getNotificationsForUser(event, user);
    }

    /**
     * The role of this method is to get the upcoming events for a user
     *
     * @param userDTO - the user
     * @return the events of the user
     */
    public List<EventDTO> getUpcomingEvents(UserDTO userDTO) {

        return this.eventService.getUpcomingEvents(userDTO).stream().map(x -> new EventDTO(x.getId(), x.getName(), new UserDTO(x.getId(), null, null, null), x.getDescription(), x.getDate(), null)).collect(Collectors.toList());
    }

    /**
     * The role of this method is to get the received messages of a user after he logged out from our application
     *
     * @param userDTO - the user
     * @return the list of received messages
     */
    public List<MessageDTO> getAllRecentMessages(UserDTO userDTO) {

        return this.getAllMessagesForAUser(userDTO).stream().filter(x-> !x.getFrom().getUserName().equals(userDTO.getUserName()) && x.getDate().isAfter(userDTO.getLastLogin())).collect(Collectors.toList());
    }

    /**
     * The role of this method is to get the received requests of a user after he logged out from our application
     *
     * @param userDTO - the user
     * @return the list of received requests
     */
    public List<RequestDTO> getAllRecentRequests(UserDTO userDTO) {

        return this.getAllRequestsForAUser(userDTO, -1).stream().filter(x -> x.getDate().isAfter(userDTO.getLastLogin())).collect(Collectors.toList());
    }

    /**
     * Get all messages for a user by username
     *
     * @param user the user for which the messages are returned
     * @return a list which contains all the messages that a user has sent or received
     * @throws ServiceException if there is no user with the username given as parameter
     */
    public List<MessageDTO> getAllMessagesForAUser(UserDTO user) {

        String username = user.getUserName();

        this.userService.validateUsernameOrUsernames(username);

        Long id = this.userService.fromUserNameToId(username);

        if (id == null)

            throw new ServiceException("No username found with that username!\n");

        List<Message> messages = this.messageService.getMessagesForAUser(user);

        List<MessageDTO> list = new ArrayList<>();

        messages.forEach(x -> {
            User u = this.userService.findOne(new UserDTO(x.getFrom(), "Fn", "Ln", "Un"));
            List<Long> ids = x.getTo();

            List<UserDTO> l = new ArrayList<>();

            ids.forEach(y -> {
                User u1 = this.userService.findOne(new UserDTO(y, "Fn", "Ln", "Un"));
                l.add(new UserDTO(u1.getId(), u1.getFirstName(), u1.getLastName(), u1.getUserName()));
            });

            list.add(new MessageDTO(x.getId(), new UserDTO(u.getId(), u.getFirstName(), u.getLastName(), u.getUserName()), l, x.getMessage(), x.getDate(), x.getOriginalMessage()));
        });

        return list;
    }

    /**
     * Checks if a friendship exists between 2 users
     *
     * @param friendshipDTO - the friendship we are checking
     * @return true,  if it exists
     * false, otherwise
     */
    public boolean searchFriendship(FriendshipDTO friendshipDTO) {

        return this.friendshipService.findOne(friendshipDTO) != null;
    }

    /**
     * Checks if a request exists between 2 users
     *
     * @param requestDTO - the request we are checking
     * @return true, if exists
     * false, otherwise
     */
    public Request searchRequest(RequestDTO requestDTO) {

        return this.requestService.getRequest(requestDTO);
    }

    /**
     * Method for adding a profile to the repository.
     *
     * @param profile the profile that will be added
     * @param user    the user the profile belongs to
     */
    public void addProfile(ProfileDTO profile, UserDTO user) {

        Profile p = new Profile(profile.getAboutMe(), profile.getHomeTown(), profile.getBirthday(), profile.getHobbies());
        p.setId(user.getId());

        this.profileService.addProfile(p);
    }

    /**
     * Method for deleting a profile from the repository.
     *
     * @param profile the profile that will be deleted
     */
    public void deleteProfile(ProfileDTO profile) {

        Profile p = new Profile(profile.getAboutMe(), profile.getHomeTown(), profile.getBirthday(), profile.getHobbies());
        p.setId(profile.getUser().getId());

        this.profileService.deleteProfile(p);
    }

    /**
     * Method for updating a profile in the repository.
     *
     * @param profile the profile that will be updated
     */
    public void updateProfile(ProfileDTO profile) {

        Profile p = new Profile(profile.getAboutMe(), profile.getHomeTown(), profile.getBirthday(), profile.getHobbies());
        p.setId(profile.getUser().getId());

        this.profileService.updateProfile(p);
    }

    /**
     * Method used for getting a profile associated to a certain user.
     *
     * @param user the user that the profile belongs to
     * @return the profile associated to the given user
     */
    public ProfileDTO getProfileForUser(UserDTO user) {

        User u = new User(null, null, null, null);
        u.setId(user.getId());

        Profile p = this.profileService.getProfileForUser(u);

        return new ProfileDTO(user, p.getAboutMe(), p.getHomeTown(), p.getBirthday(), p.getHobbies());
    }

    /**
     * Method for inserting a post into the repository.
     *
     * @param post the post that will be inserted
     */
    public void addPost(PostDTO post) {

        Post p = new Post(post.getUser().getId(), post.getContent(), post.getPostedOn());

        this.postService.addPost(p);
    }

    /**
     * Method for deleting a post from the repository.
     *
     * @param post the post that will be deleted
     */
    public void deletePost(PostDTO post) {

        Post p = new Post(post.getUser().getId(), post.getContent(), post.getPostedOn());
        p.setId(post.getId());

        this.postService.deletePost(p);
    }

    /**
     * Method for updating a post in the repository.
     *
     * @param post the post that will be updated
     */
    public void updatePost(PostDTO post) {

        Post p = new Post(post.getUser().getId(), post.getContent(), post.getPostedOn());
        p.setId(post.getId());

        this.postService.updatePost(p);
    }

    /**
     * Method for returning all the posts of a given user.
     *
     * @param user the user for which the posts are returned
     * @return a list of posts for the given user, sorted chronologically
     */
    public List<PostDTO> getAllPostsForUser(UserDTO user) {

        List<Post> posts = this.postService.getAllPostsForUser(user);

        List<PostDTO> result = new ArrayList<>();

        for (Post p : posts) {

            result.add(new PostDTO(p.getId(), user, p.getContent(), p.getPostedOn()));
        }

        return result;

    }

    /**
     * Method used for getting a user's feed.
     *
     * @param page the page of the user
     * @return a list containing posts and events from the user's friends, sorted chronologically
     */
    public List<Object> getFeedForUser(Page page) {

        List<Object> list = new ArrayList<>();

        List<UserDTO> friends = page.getFriendsList();
        List<EventDTO> events = this.getAllEvents(-1);

        List<Tuple<UserDTO, EventDTO>> eventList = new ArrayList<>();
        List<PostDTO> postList = new ArrayList<>();

        for (UserDTO u : friends) {

            List<PostDTO> posts = this.getAllPostsForUser(u);

            postList.addAll(posts);

            for (EventDTO e : events)
                if (e.getParticipants().contains(u))
                    eventList.add(new Tuple<>(u, e));
        }

        eventList.sort(Comparator.comparing(x -> x.getRight().getDate(), Comparator.reverseOrder()));

        int i = 0, j = 0;

        while (i < postList.size() && j < eventList.size()) {

            if (postList.get(i).getPostedOn().isAfter(eventList.get(j).getRight().getDate())) {

                list.add(postList.get(i));
                i++;
            } else if (postList.get(i).getPostedOn().isBefore(eventList.get(j).getRight().getDate())) {
                list.add(eventList.get(j));
                j++;
            } else {

                list.add(postList.get(i));
                list.add(eventList.get(j));
                i++;
                j++;
            }
        }

        while (i < postList.size()) {

            list.add(postList.get(i));
            i++;
        }

        while (j < eventList.size()) {

            list.add(eventList.get(j));
            j++;
        }

        return list;
    }

    /**
     * Method used for getting the post service of the super service.
     *
     * @return the post service
     */
    public PostService getPostService() {

        return this.postService;
    }

    /**
     * Method for deleting old events.
     */
    public void deleteOldEvents() {

        this.eventService.deleteOldEvents();
    }

}