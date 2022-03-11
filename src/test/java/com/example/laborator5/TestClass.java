package com.example.laborator5;

import com.example.laborator5.socialnetwork.Main;
import com.example.laborator5.socialnetwork.domain.*;
import com.example.laborator5.socialnetwork.domain.validators.*;

import com.example.laborator5.socialnetwork.network.Graph;
import com.example.laborator5.socialnetwork.repository.Repository;
import com.example.laborator5.socialnetwork.repository.database.*;
import com.example.laborator5.socialnetwork.repository.database.hikari.DataSource;
import com.example.laborator5.socialnetwork.repository.paging.PageableImplementation;
import com.example.laborator5.socialnetwork.repository.paging.PagingRepository;
import com.example.laborator5.socialnetwork.service.*;
import com.example.laborator5.socialnetwork.service.dto.*;
import com.example.laborator5.socialnetwork.utils.MD5;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestClass {

    @BeforeAll
    static void beforeAll() {

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("test.properties")) {

            Properties properties = new Properties();

            if (input == null) {

                System.out.println("Error reading from properties file!");
                return;
            }

            properties.load(input);

            System.setProperty("jdbc_url", properties.getProperty("db.url"));

            System.out.println("Running tests...");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @AfterAll
    static void afterAll() {

        System.out.println("Finished running tests!");
    }

    @AfterEach
    public void afterEach() throws SQLException {

        Connection connection = DataSource.getConnection();

        PreparedStatement ps = connection.prepareStatement("DELETE FROM users;");

        ps.executeUpdate();

        connection.close();

    }

    @Test
    public void entityTest() {

        Entity<Long> e = new Entity<>();
        e.setId(1L);
        assertEquals(e.getId(), 1L);
    }

    @Test
    public void tupleTest() {

        Tuple<Integer, Integer> t = new Tuple<>(1, 2);
        assertEquals(t.getLeft(), 1);
        assertEquals(t.getRight(), 2);
        t.setLeft(2);
        t.setRight(1);
        assertEquals(t.getLeft(), 2);
        assertEquals(t.getRight(), 1);

        assertEquals(t.toString(), "2,1");
        Tuple<Integer, Integer> t1 = new Tuple<>(2, 1);
        assertEquals(t, t1);
        assertEquals(t.hashCode(), Objects.hash(2, 1));
    }

    @Test
    public void userTest() {

        User user = new User("FN", "LN", "U1", "P1");

        assertEquals(user.getPassword(), "P1");
        assertEquals(user.getFirstName(), "FN");
        assertEquals(user.getLastName(), "LN");
        assertEquals(user.getUserName(), "U1");

        user.setFirstName("Stefan");
        user.setLastName("Farca");
        user.setUserName("U");
        user.setPassword("P2");

        assertEquals(user.getPassword(), "P2");
        assertEquals(user.getFirstName(), "Stefan");
        assertEquals(user.getLastName(), "Farca");
        assertEquals(user.getUserName(), "U");

        user.setId(1L);

        assertEquals(user.toString(), "FirstName = Stefan, LastName = Farca, UserName = U");
        assertEquals(user, user);

        Entity<Long> e = new Entity<>();
        assertNotEquals(user, e);

        User u = new User("Stefan", "Farca", "U2", "P1");
        u.setId(1L);

        User newUser = new User("FN", "LN", "FNLN", "P1");
        Long o1 = 1L;
        Long o2 = 2L;
        Long o3 = 3L;

        Validator<User> userValidator = new UserValidator();
        assertThrows(ValidationException.class, () -> userValidator.validate(new User(null, null, null, "P1")));
        assertThrows(ValidationException.class, () -> userValidator.validate(new User("", "", "", "P1")));
        assertThrows(ValidationException.class, () -> userValidator.validate(new User("1", "1", "a-b", "P1")));
    }

    @Test
    public void friendhsipTest() {

        String DataS = "2020-10-10 10:30";
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Friendship friendship = new Friendship(1L, 2L, LocalDateTime.parse(DataS, f));

        Friendship friendship2 = new Friendship(1L, 2L);
        Friendship friendship3 = new Friendship(1L, 1L);

        FriendshipValidator validator = new FriendshipValidator();
        ValidationException thrown = assertThrows(ValidationException.class, () -> validator.validate(friendship3));

        LocalDateTime date = LocalDateTime.now();
        Friendship friendship1 = new Friendship(2L, 1L, date);
        friendship1.setDate(date);

        assertEquals(friendship1.getDate(), date);
        assertEquals(friendship1.toString(), "ID1 = 1, ID2 = 2, Data = " + date);
    }

    @Test
    public void pageTest() {

        Page page = new Page(new UserDTO(1L, "Popsecu", "Andrei", "popel"), List.of(new UserDTO(2L, "Frone", "Gigel", "fogi")), List.of(new MessageDTO(1L, new UserDTO(2L, "Frone", "Gigel", "fogi"), List.of(new UserDTO(1L, "Popsecu", "Andrei", "popel")), "Salut frate", LocalDateTime.now(), null)), List.of(new RequestDTO(new UserDTO(1L, "Popsecu", "Andrei", "popel"), new UserDTO(3L, "Popsecu", "Gicu", "popi"), LocalDateTime.now(), "pending")), List.of(new EventDTO(1L, "BRC", new UserDTO(10L, "Mucu", "Tucu", "mutu"), "Rock Music Festival", LocalDateTime.of(2026, 8, 25, 12, 0), null), new EventDTO(2L, "Christmas", new UserDTO(11L, "Mos", "Craciun", "mocu"), "Worldwide Holiday", LocalDateTime.of(2035, 12, 25, 22, 0), null)));

        assertEquals(page.getUser().getId(), 1L);
        assertEquals(page.getFriendsList().size(), 1);
        assertEquals(page.getMessages().size(), 1);
        assertEquals(page.getRequests().size(), 1);
        assertEquals(2, page.getEvents().size());

        page.setUser(new UserDTO(4L, "Fanel", "Junior", "fanica"));
        assertEquals(page.getUser().getId(), 4L);

        page.setFriendsList(List.of(new UserDTO(5L, "Maricica", "Dansatoarea", "mariD"), new UserDTO(6L, "Mari", "Dan", "danM")));
        assertEquals(page.getFriendsList().size(), 2);

        page.setMessages(List.of(new MessageDTO(1L, new UserDTO(5L, "Maricica", "Dansatoarea", "mariD"), List.of(new UserDTO(4L, "Fanel", "Junior", "fanica")), "Salut sora", LocalDateTime.now(), null),
                new MessageDTO(2L, new UserDTO(6L, "Mari", "Dan", "danM"), List.of(new UserDTO(4L, "Fanel", "Junior", "fanica")), "Salut fanica", LocalDateTime.now(), null)));
        assertEquals(page.getMessages().size(), 2);

        page.setRequests(List.of(new RequestDTO(new UserDTO(4L, "Fanel", "Junior", "fanica"), new UserDTO(3L, "Popsecu", "Gicu", "popi"), LocalDateTime.now(), "pending"),
                new RequestDTO(new UserDTO(4L, "Fanel", "Junior", "fanica"), new UserDTO(1L, "Popsecu", "Andrei", "popel"), LocalDateTime.now(), "pending")));
        assertEquals(page.getRequests().size(), 2);
    }

    @Test
    public void profileTest() {

        Profile p1 = new Profile("CS Student, UBB Cluj-Napoca", "Suceava", LocalDate.of(2002, 4, 5), "Programming, Airplanes");

        p1.setId(2L);
        assertEquals(2L, p1.getId());
        assertEquals("CS Student, UBB Cluj-Napoca", p1.getAboutMe());
        assertEquals("Suceava", p1.getHomeTown());
        assertEquals(LocalDate.of(2002, 4, 5), p1.getBirthday());
        assertEquals("Programming, Airplanes", p1.getHobbies());

        p1.setId(1L);
        p1.setAboutMe("2nd year Student, FMI UBB");
        p1.setHomeTown("Rm. Valcea");
        p1.setBirthday(LocalDate.of(2001, 8, 20));
        p1.setHobbies("Programming, Formula 1");

        assertEquals(1L, p1.getId());
        assertEquals("2nd year Student, FMI UBB", p1.getAboutMe());
        assertEquals("Rm. Valcea", p1.getHomeTown());
        assertEquals(LocalDate.of(2001, 8, 20), p1.getBirthday());
        assertEquals("Programming, Formula 1", p1.getHobbies());

        Profile p2 = new Profile("blabla", "Pocreaca", LocalDate.of(2000, 12, 16), "Blabla");

        p2.setId(1L);
        assertEquals(p1, p2);

    }

    @Test
    public void postTest() {

        Post p1 = new Post(1L, "Mi-am luat bemveu", LocalDateTime.of(2021, 12, 9, 12, 0));
        p1.setId(1L);

        assertEquals(1L, p1.getId());
        assertEquals(1L, p1.getIdUser());
        assertEquals("Mi-am luat bemveu", p1.getContent());
        assertEquals(LocalDateTime.of(2021, 12, 9, 12, 0), p1.getPostedOn());

        p1.setIdUser(2L);
        p1.setContent("Mi-am luat mertan");
        p1.setPostedOn(LocalDateTime.of(2020, 1, 1, 13, 50));

        assertEquals(1L, p1.getId());
        assertEquals(2L, p1.getIdUser());
        assertEquals("Mi-am luat mertan", p1.getContent());
        assertEquals(LocalDateTime.of(2020, 1, 1, 13, 50), p1.getPostedOn());

        Post p2 = new Post(1L, "Mi-am luat bemveu", LocalDateTime.of(2021, 12, 9, 12, 0));
        p2.setId(1L);

        assertEquals(p1, p2);

    }

    @Test
    public void dtoTest() {

        User user1 = new User("A", "A", "A", "P1");
        user1.setId(1L);
        UserDTO userDTO1 = new UserDTO(user1.getId(), user1.getFirstName(), user1.getLastName(), user1.getUserName());
        User user2 = new User("B", "B", "B", "P1");
        user2.setId(2L);
        UserDTO userDTO2 = new UserDTO(user2.getId(), user2.getFirstName(), user2.getLastName(), user2.getUserName());

        assertEquals(userDTO2.getUserName(), "B");
        assertEquals(userDTO2.getFirstName(), "B");
        assertEquals(userDTO2.getLastName(), "B");

        assertEquals(userDTO1.getId(), user1.getId());
        assertEquals(userDTO1.toString(), "A A (A)");

        assertEquals(userDTO2.getId(), user2.getId());
        assertEquals(userDTO2.toString(), "B B (B)");

        LocalDateTime date = LocalDateTime.now();
        FriendshipDTO friendshipDTO = new FriendshipDTO(userDTO1, userDTO2, date);
        friendshipDTO.getUser1().equals(userDTO1);
        friendshipDTO.getUser2().equals(userDTO2);
        friendshipDTO.getDate().equals(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        friendshipDTO.toString().equals("A A is friend with B B since " + date.format(formatter));

        User u1 = new User("A", "B", "ab", "P1");
        User u2 = new User("C", "D", "cd", "P1");

        u1.setId(1L);
        u2.setId(2L);

        UserDTO uDTO1 = new UserDTO(1L, "A", "B", "ab");
        UserDTO uDTO2 = new UserDTO(2L, "C", "D", "cd");
        UserDTO uDTO3 = new UserDTO(3L, "E", "F", "ef");

        RequestDTO requestDTO = new RequestDTO(uDTO1, uDTO2, LocalDateTime.of(2020, 11, 17, 15, 6), "pending");

        assertEquals(requestDTO.getUser1().getId(), 1L);
        assertEquals(requestDTO.getUser2().getId(), 2L);
        assertEquals(requestDTO.getDate(), LocalDateTime.of(2020, 11, 17, 15, 6));
        assertEquals(requestDTO.getStatus(), "pending");

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        requestDTO.toString().equals("ab requested to be friends with cd on " + date.format(formatter));

        MessageDTO messageDTO = new MessageDTO(1L, uDTO1, List.of(uDTO2, uDTO3), "salut", LocalDateTime.now(), 2L);

        ConversationDTO conversationDTO = new ConversationDTO("Stefan Farcasanu", List.of(new UserDTO(1L, null, null, "stefan4556"), new UserDTO(2L, null, null, "vic")), new MessageDTO(null, null, null, "eu fac bine, tu ce faci", LocalDateTime.now(), null));

        assertEquals(conversationDTO.getFromString(), "Stefan Farcasanu");
        assertEquals(conversationDTO.getLatestMessage().getMessage(), "eu fac bine, tu ce faci");
        assertEquals(conversationDTO.getParticipants().size(), 2);

        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(new UserDTO(1L, "FN", "LN", "UN"), "password");
        assertEquals(userRegisterDTO.getUser().getId(), 1L);
        assertEquals(userRegisterDTO.getUser().getFirstName(), "FN");
        assertEquals(userRegisterDTO.getUser().getLastName(), "LN");
        assertEquals(userRegisterDTO.getUser().getUserName(), "UN");
        assertEquals(userRegisterDTO.getPassword(), "password");

        LocalDateTime dateTime = LocalDateTime.parse("2020-10-10 10:10:00", formatter);
        EventDTO eventDTO = new EventDTO(1L, "Untold", new UserDTO(1L, null, null, null), "Festival", dateTime, List.of(new UserDTO(2L, null, null, null), new UserDTO(3L, null, null, null)));

        assertEquals(1L, eventDTO.getId());
        assertEquals("Untold", eventDTO.getName());
        assertEquals(1L, eventDTO.getOrganizer().getId());
        assertEquals("Festival", eventDTO.getDescription());
        assertEquals(dateTime, eventDTO.getDate());
        assertEquals(2L, eventDTO.getParticipants().get(0).getId());
        assertEquals(3L, eventDTO.getParticipants().get(1).getId());

        eventDTO.setId(2L);
        assertEquals(2L, eventDTO.getId());

        eventDTO.setName("Neversea");
        assertEquals("Neversea", eventDTO.getName());

        eventDTO.setOrganizer(new UserDTO(2L, null, null, null));
        assertEquals(2L, eventDTO.getOrganizer().getId());

        eventDTO.setDescription("Party");
        assertEquals("Party", eventDTO.getDescription());

        LocalDateTime localDateTime = LocalDateTime.parse("2021-10-10 10:11:00", formatter);

        eventDTO.setDate(localDateTime);
        assertEquals(localDateTime, eventDTO.getDate());

        eventDTO.setParticipants(List.of(new UserDTO(1L, null, null, null)));
        assertEquals(1L, eventDTO.getParticipants().get(0).getId());

        ProfileDTO profileDTO = new ProfileDTO(new UserDTO(1L, "A", "B", "ab"), "Student", "Suceava", LocalDate.of(2001, 5, 21), "Programming");

        assertEquals(1L, profileDTO.getUser().getId());
        assertEquals("A", profileDTO.getUser().getFirstName());
        assertEquals("B", profileDTO.getUser().getLastName());
        assertEquals("ab", profileDTO.getUser().getUserName());

        assertEquals("Student", profileDTO.getAboutMe());
        assertEquals("Suceava", profileDTO.getHomeTown());
        assertEquals(LocalDate.of(2001, 5, 21), profileDTO.getBirthday());
        assertEquals("Programming", profileDTO.getHobbies());

        profileDTO.setUser(new UserDTO(2L, "C", "D", "cd"));
        profileDTO.setAboutMe("2nd year student");
        profileDTO.setHomeTown("Brosteni");
        profileDTO.setBirthday(LocalDate.of(2001, 1, 1));
        profileDTO.setHobbies("Not Programming");

        assertEquals(2L, profileDTO.getUser().getId());
        assertEquals("C", profileDTO.getUser().getFirstName());
        assertEquals("D", profileDTO.getUser().getLastName());
        assertEquals("cd", profileDTO.getUser().getUserName());

        assertEquals("2nd year student", profileDTO.getAboutMe());
        assertEquals("Brosteni", profileDTO.getHomeTown());
        assertEquals(LocalDate.of(2001, 1, 1), profileDTO.getBirthday());
        assertEquals("Not Programming", profileDTO.getHobbies());

        ProfileDTO profileDTO2 = new ProfileDTO(new UserDTO(2L, "X", "Y", "xy"), "baba", "lala", LocalDate.of(2001, 8, 30), "bubu");

        assertEquals(profileDTO, profileDTO2);

        PostDTO postDTO = new PostDTO(1L, new UserDTO(1L, "a", "b", "ab"), "Mi-e foame", LocalDateTime.of(2021, 12, 24, 9, 1));

        assertEquals(1L, postDTO.getId());

        assertEquals(1L, postDTO.getUser().getId());
        assertEquals("a", postDTO.getUser().getFirstName());
        assertEquals("b", postDTO.getUser().getLastName());
        assertEquals("ab", postDTO.getUser().getUserName());

        assertEquals("Mi-e foame", postDTO.getContent());
        assertEquals(LocalDateTime.of(2021, 12, 24, 9, 1), postDTO.getPostedOn());

        postDTO.setId(2L);
        postDTO.setUser(new UserDTO(2L, "c", "d", "cd"));
        postDTO.setContent("Mi-e sete");
        postDTO.setPostedOn(LocalDateTime.of(2021, 5, 6, 7, 8));

        assertEquals(2L, postDTO.getId());

        assertEquals(2L, postDTO.getUser().getId());
        assertEquals("c", postDTO.getUser().getFirstName());
        assertEquals("d", postDTO.getUser().getLastName());
        assertEquals("cd", postDTO.getUser().getUserName());

        assertEquals("Mi-e sete", postDTO.getContent());
        assertEquals(LocalDateTime.of(2021, 5, 6, 7, 8), postDTO.getPostedOn());

        PostDTO postDTO2 = new PostDTO(2L, new UserDTO(1L, "a", "b", "ab"), "Mi-e foame", LocalDateTime.of(2021, 12, 24, 9, 1));

        assertEquals(postDTO, postDTO2);

    }

    @Test
    public void messageTest() {

        List<Long> list1 = Arrays.asList(2L, 3L);
        Message message1 = new Message(1L, 1L, list1, "salut");

        assertEquals(message1.getId(), 1L);
        assertEquals(message1.getFrom(), 1L);
        assertEquals(message1.getMessage(), "salut");
        assertEquals(message1.getTo(), list1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime date = LocalDateTime.parse("2021-11-11 11:11", formatter);
        message1.setDate(date);
        assertEquals(message1.getDate(), date);
        assertNull(message1.getOriginalMessage());

        List<Long> list2 = Arrays.asList(1L);

        Message message2 = new Message(2L, 2L, list2, "servus", date);
        message2.setOriginalMessage(1L);
        assertEquals(message2.getOriginalMessage(), 1L);

        message2.setFrom(3L);
        assertEquals(message2.getFrom(), 3L);

        message2.setTo(list1);
        assertEquals(message2.getTo(), list1);

        message2.setMessage("servus draga");
        assertEquals(message2.getMessage(), "servus draga");

        MessageValidator validator = new MessageValidator();
        assertThrows(ValidationException.class, () -> validator.validate(new Message(1L, null, null, null)));
        assertThrows(ValidationException.class, () -> validator.validate(new Message(1L, 1L, Arrays.asList(2L), "")));
        assertThrows(ValidationException.class, () -> validator.validate(new Message(null, 1L, Arrays.asList(2L), "da")));
        assertThrows(ValidationException.class, () -> validator.validate(new Message(-1L, 1L, Arrays.asList(2L), "da")));
    }

    @Test
    public void eventTest() {

        LocalDateTime date = LocalDateTime.parse("2020-10-10 10:10", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Event event = new Event(1L, "Untold", 1L, "Music festival", date, List.of(1L, 2L, 3L));

        assertEquals(1L, event.getId());
        assertEquals("Untold", event.getName());
        assertEquals(1L, event.getOrganizer());
        assertEquals("Music festival", event.getDescription());
        assertEquals(date, event.getDate());
        assertEquals(List.of(1L, 2L, 3L), event.getParticipants());

        event.setId(2L);
        event.setName("Neversea");
        event.setOrganizer(2L);
        event.setDescription("Beach music festival");
        LocalDateTime date2 = LocalDateTime.parse("2021-10-10 10:10", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        event.setDate(date2);
        event.setParticipants(List.of(1L, 2L));

        assertEquals(2L, event.getId());
        assertEquals("Neversea", event.getName());
        assertEquals(2L, event.getOrganizer());
        assertEquals("Beach music festival", event.getDescription());
        assertEquals(date2, event.getDate());
        assertEquals(List.of(1L, 2L), event.getParticipants());

        EventValidator eventValidator = new EventValidator();
        Event event1 = new Event(1L, null, 1L, null, null, List.of(1L));

        assertThrows(ValidationException.class, () -> eventValidator.validate(event1));

        Event event2 = new Event(1L, "", 1L, "", date2, List.of(1L));

        assertThrows(ValidationException.class, () -> eventValidator.validate(event2));
    }

    @Test
    public void userDBTest() {

        Repository<Long, User> userRepository = new UserDB();

        User usr1 = new User("Stefan", "Farcasanu", "stefan4556", "P1");
        usr1.setId(1L);

        User usr2 = new User("Victor", "Doroftei", "vic", "P2");
        usr2.setId(2L);

        User usr3 = new User("Radu", "Nita", "foca", "P3");
        usr3.setId(3L);

        User usr4 = new User("Andrei", "Covaciu", "ake", "P4");
        usr4.setId(4L);

        User usr5 = new User("Alex", "Gheorghe", "mattix", "P5");
        usr5.setId(5L);

        userRepository.save(usr1);
        userRepository.save(usr2);
        userRepository.save(usr3);
        userRepository.save(usr4);
        userRepository.save(usr5);

        Iterable<User> l = userRepository.findAll();
        int ct = 0;
        for (User u : l)
            ct++;

        assertEquals(ct, 5);

        User u = userRepository.findOne(1L);
        assertEquals(u.getUserName(), "stefan4556");

        User u2 = userRepository.findOne(20L);
        assertNull(u2);

        User u3 = new User("a", "a", "a", "P1");
        u3.setId(6L);
        userRepository.save(u3);
        ct = 0;
        l = userRepository.findAll();
        for (User u1 : l)
            ct++;

        assertEquals(ct, 6);

        User u6 = new User(null, null, null, null);
        u6.setId(6L);

        userRepository.delete(u6);
        ct = 0;
        l = userRepository.findAll();
        for (User u1 : l)
            ct++;

        assertEquals(ct, 5);

        LocalDateTime ldt1 = LocalDateTime.of(2020, 8, 15, 12, 0);
        User u4 = new User("b", "b", "b", "P1", ldt1);
        u4.setId(1L);
        userRepository.update(u4);

        User u5 = userRepository.findOne(1L);
        assertEquals(u5.getUserName(), "b");
        assertEquals(ldt1, u5.getLastLogin());

        User u8 = new User("Stefan", "Farcasanu", "stefan4556", "P1", null);
        u8.setId(1L);
        userRepository.update(u8);
        assertEquals(userRepository.findOne(1L).getUserName(), "stefan4556");
    }

    @Test
    public void friendshipDBTest() {

        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = new FriendshipDB();

        Repository<Long, User> userRepository = new UserDB();

        User u2 = new User("Victor", "Doroftei", "vic", "P2");
        u2.setId(2L);

        User u3 = new User("Radu", "Nita", "foca", "P3");
        u3.setId(3L);

        userRepository.save(u2);
        userRepository.save(u3);

        Friendship f = new Friendship(2L, 3L);
        friendshipRepository.save(f);

        Iterable<Friendship> l = friendshipRepository.findAll();
        int ct = 0;
        for (Friendship ff : l)
            ct++;
        assertEquals(ct, 1);

        friendshipRepository.delete(new Friendship(2L, 3L));

        l = friendshipRepository.findAll();
        ct = 0;
        for (Friendship ff : l)
            ct++;
        assertEquals(ct, 0);

        f = new Friendship(2L, 3L);
        friendshipRepository.save(f);

        LocalDateTime date = LocalDateTime.of(2020, 10, 10, 10, 10, 10);
        Friendship fu = new Friendship(2L, 3L, date);

        friendshipRepository.update(fu);
        Friendship test = friendshipRepository.findOne(new Tuple<>(2L, 3L));
        assertEquals(test.getDate().getYear(), 2020);

        LocalDateTime dateTime = LocalDateTime.of(2021, 11, 7, 21, 29, 0);
        Friendship friendship = new Friendship(2L, 3L, dateTime);
        friendshipRepository.update(friendship);
        test = friendshipRepository.findOne(new Tuple<>(2L, 3L));
        assertEquals(test.getDate().getYear(), 2021);
    }

    @Test
    public void requestDBTest() {

        Repository<Long, Request> requestRepository = new RequestDB();

        Repository<Long, User> userRepository = new UserDB();

        User u1 = new User("Stefan", "Farcasanu", "stefan4556", "P1");
        u1.setId(1L);

        User u2 = new User("Victor", "Doroftei", "vic", "P2");
        u2.setId(2L);

        User u3 = new User("Radu", "Nita", "foca", "P3");
        u3.setId(3L);

        User u4 = new User("Andrei", "Covaciu", "ake", "P4");
        u4.setId(4L);

        User u5 = new User("Alex", "Gheorghe", "mattix", "P5");
        u5.setId(5L);

        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);
        userRepository.save(u4);
        userRepository.save(u5);

        LocalDateTime ldt = LocalDateTime.of(2021, 9, 15, 18, 55);
        Request r1 = new Request(1L, 1L, 2L, ldt, "pending");
        Request r2 = new Request(2L, 1L, 3L, ldt, "pending");
        Request r3 = new Request(3L, 2L, 3L, ldt, "pending");

        requestRepository.save(r1);
        requestRepository.save(r2);
        requestRepository.save(r3);

        Iterable<Request> iterable = requestRepository.findAll();

        int size = 0;
        for (Request r : iterable)
            size++;

        assertEquals(size, 3);

        Request res = requestRepository.findOne(1L);
        assertEquals(res.getId(), 1L);
        assertEquals(res.getIdUser1(), 1L);
        assertEquals(res.getIdUser2(), 2L);
        assertEquals(res.getDate().getYear(), 2021);

        Month m = Month.SEPTEMBER;
        assertEquals(res.getDate().getMonth(), m);
        assertEquals(res.getDate().getDayOfMonth(), 15);
        assertEquals(res.getDate().getHour(), 18);
        assertEquals(res.getDate().getMinute(), 55);
        assertEquals(res.getStatus().getStatus(), "pending");

        requestRepository.delete(new Request(2L, null, null, null));
        iterable = requestRepository.findAll();

        size = 0;
        for (Request r : iterable)
            size++;

        assertEquals(size, 2);

        requestRepository.delete(new Request(3L, null, null, null));
        requestRepository.delete(new Request(1L, null, null, null));

        size = 0;
        iterable = requestRepository.findAll();
        for (Request r : iterable)
            size++;

        assertEquals(size, 0);
    }

    @Test
    public void graphDBTest() {

        Repository<Long, User> userRepository = new UserDB();
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = new FriendshipDB();

        User u1 = new User("Stefan", "Farcasanu", "stefan4556", "P1");
        u1.setId(1L);

        User u2 = new User("Victor", "Doroftei", "vic", "P2");
        u2.setId(2L);

        User u3 = new User("Radu", "Nita", "foca", "P3");
        u3.setId(3L);

        User u4 = new User("Andrei", "Covaciu", "ake", "P4");
        u4.setId(4L);

        User u5 = new User("Alex", "Gheorghe", "mattix", "P5");
        u5.setId(5L);

        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);
        userRepository.save(u4);
        userRepository.save(u5);

        friendshipRepository.save(new Friendship(3L, 4L));
        friendshipRepository.save(new Friendship(1L, 5L));
        friendshipRepository.save(new Friendship(1L, 2L));

        Graph graph = new Graph(userRepository.findAll(), friendshipRepository.findAll());

        List<List<Long>> connectedComponents = graph.connectedComponents();
        assertEquals(connectedComponents.size(), 2);

        List<Long> longestPath = graph.getTheMostSociableConnection();
        assertEquals(longestPath.size(), 3);
    }

    @Test
    public void messageDBTest() {

        Repository<Long, Message> messageRepository = new MessageDB();
        Repository<Long, User> userRepository = new UserDB();

        User u1 = new User("a", "b", "ab", null);
        u1.setId(1L);

        userRepository.save(u1);

        User u2 = new User("a", "b", "cd", null);
        u2.setId(2L);
        userRepository.save(u2);

        u1 = new User("a", "b", "ef", null);
        u1.setId(3L);

        userRepository.save(u1);

        u2 = new User("a", "b", "gh", null);
        u2.setId(4L);
        userRepository.save(u2);

        messageRepository.save(new Message(1L, 1L, Arrays.asList(2L, 3L), "salut"));
        messageRepository.save(new Message(2L, 2L, Arrays.asList(3L, 4L), "salut1"));

        Iterable<Message> messages = messageRepository.findAll();

        int ct = 0;

        for (Message m : messages)

            ct++;

        assertEquals(ct, 2);

        Message message = messageRepository.findOne(1L);

        assertEquals(message.getTo().size(), 2);
        assertEquals(message.getFrom(), 1L);
        assertEquals(message.getId(), 1L);
        assertNull(message.getOriginalMessage());

        messageRepository.delete(new Message(2L, null, null, null));
        messageRepository.delete(new Message(1L, null, null, null));
    }

    @Test
    public void eventDBTest() {

        EventDB eventDB = new EventDB();

        Repository<Long, User> userRepository = new UserDB();

        User u1 = new User("a", "b", "ab", null);
        u1.setId(1L);

        userRepository.save(u1);

        User u2 = new User("a", "b", "cd", null);
        u2.setId(2L);
        userRepository.save(u2);

        User u3 = new User("a", "b", "ef", null);
        u3.setId(3L);
        userRepository.save(u3);

        LocalDateTime date2 = LocalDateTime.parse("2021-10-10 10:10", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        eventDB.save(new Event(1L, "Untold", 1L, "Music Festival", date2, null));

        Event event = eventDB.findOne(1L);

        assertEquals(1L, event.getId());
        assertEquals("Untold", event.getName());
        assertEquals(1L, event.getOrganizer());
        assertEquals("Music Festival", event.getDescription());
        assertEquals(date2, event.getDate());
        assertEquals(1, event.getParticipants().size());

        int ct = 0;
        Iterable<Event> events = eventDB.findAll();

        for (Event e : events)

            ct++;

        assertEquals(1, ct);

        LocalDateTime date = LocalDateTime.parse("2020-10-10 10:10", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        eventDB.update(new Event(1L, "Neversea", 2L, "Beach Music Festival", date, null));

        event = eventDB.findOne(1L);

        assertEquals(1L, event.getId());
        assertEquals("Neversea", event.getName());
        assertEquals("Beach Music Festival", event.getDescription());
        assertEquals(date, event.getDate());
        assertEquals(1, event.getParticipants().size());

        EventParticipantsDB eventParticipantsDB = new EventParticipantsDB();

        eventParticipantsDB.save(new Event(1L, null, null, null, null, List.of(2L)));

        eventParticipantsDB.save(new Event(1L, null, null, null, null, List.of(3L)));

        event = eventDB.findOne(1L);

        assertEquals(3, event.getParticipants().size());

        eventParticipantsDB.delete(new Event(1L, null, null, null, null, List.of(2L)));

        event = eventDB.findOne(1L);

        assertEquals(2, event.getParticipants().size());

        eventDB.delete(new Event(1L, null, null, null, null, null));

        events = eventDB.findAll();

        ct = 0;

        for (Event e : events)

            ct++;

        assertEquals(0, ct);

        eventDB.save(new Event(1L, "Untold", 1L, "Muzica", LocalDateTime.now(), null));
        eventDB.save(new Event(2L, "Neversea", 2L, "Muzica mare", LocalDateTime.now(), null));
        eventDB.save(new Event(3L, "Electric", 3L, "Muzica electornica", date, null));

        eventParticipantsDB.save(new Event(2L, null, null, null, null, List.of(1L)));
        eventParticipantsDB.save(new Event(3L, null, null, null, null, List.of(1L)));

        User user = new User(null, null, null, null);
        user.setId(1L);
        List<Event> eventList = eventParticipantsDB.getUpcomingEvents(user);
        assertEquals(2, eventList.size());

        eventParticipantsDB.setNotificationsForParticipant(new Event(2L, "Neversea", 2L, "Muzica mare", LocalDateTime.now(), List.of(1L)), false);
        assertEquals(1, eventParticipantsDB.getUpcomingEvents(user).size());

        eventParticipantsDB.setNotificationsForParticipant(new Event(2L, "Neversea", 2L, "Muzica mare", LocalDateTime.now(), List.of(1L)), true);
        assertEquals(2, eventParticipantsDB.getUpcomingEvents(user).size());
    }

    @Test
    public void profileDBTest() {

        Repository<Long, Profile> profileDB = new ProfileDB();
        Repository<Long, User> userDB = new UserDB();

        User u = new User("Victor", "Doroftei", "vic", "P1");
        u.setId(1L);
        userDB.save(u);

        User u2 = new User("Stefan", "Farcasanu", "stefan", "P2");
        u2.setId(3L);
        userDB.save(u2);

        Profile p1 = new Profile("CS Student", "Cluj-Napoca", LocalDate.of(2002, 4, 5), "Arts");
        p1.setId(1L);

        profileDB.save(p1);

        Iterable<Profile> l = profileDB.findAll();
        int counter = 0;
        for (Profile ignored : l)
            counter++;

        assertEquals(1, counter);

        Profile p = profileDB.findOne(1L);
        assertEquals("CS Student", p.getAboutMe());

        p = profileDB.findOne(100L);
        assertNull(p);

        Profile p2 = new Profile("Med Student", "Iasi", LocalDate.of(2000, 12, 26), "Music");
        p2.setId(3L);
        profileDB.save(p2);

        counter = 0;
        l = profileDB.findAll();
        for (Profile ignored : l)
            counter++;
        assertEquals(2, counter);

        Profile p3 = new Profile(null, null, null, null);
        p3.setId(3L);
        profileDB.delete(p3);

        counter = 0;
        l = profileDB.findAll();
        for (Profile ignored : l)
            counter++;
        assertEquals(1, counter);

        LocalDate ld = LocalDate.of(2000, 2, 2);
        Profile p4 = new Profile("Art Student", "Bucuresti", ld, "Books");
        p4.setId(1L);
        profileDB.update(p4);

        p = profileDB.findOne(1L);
        assertEquals("Art Student", p.getAboutMe());
        assertEquals("Bucuresti", p.getHomeTown());
        assertEquals(ld, p.getBirthday());
        assertEquals("Books", p.getHobbies());

    }

    @Test
    public void postDBTest() {

        Repository<Long, Post> postDB = new PostDB();
        Repository<Long, User> userDB = new UserDB();

        User u = new User("Victor", "Doroftei", "vic", "P1");
        u.setId(1L);
        userDB.save(u);

        User u2 = new User("Stefan", "Farcasanu", "stefan", "P2");
        u2.setId(2L);
        userDB.save(u2);

        Post p1 = new Post(1L, "Post1", LocalDateTime.of(2020, 1, 1, 12, 0));
        p1.setId(1L);

        Post p2 = new Post(2L, "Post2", LocalDateTime.of(2021, 1, 1, 12, 0));
        p2.setId(2L);

        postDB.save(p1);
        postDB.save(p2);

        Iterable<Post> l = postDB.findAll();
        int counter = 0;
        for (Post ignored : l)
            counter++;
        assertEquals(2, counter);

        Post p = postDB.findOne(1L);
        assertEquals("Post1", p.getContent());

        p = postDB.findOne(2L);
        assertEquals("Post2", p.getContent());
        assertEquals(LocalDateTime.of(2021, 1, 1, 12, 0), p.getPostedOn());

        Post p3 = new Post(2L, "Post3", LocalDateTime.of(2022, 1, 1, 12, 0));
        p3.setId(2L);

        postDB.update(p3);
        p = postDB.findOne(2L);
        assertEquals("Post3", p.getContent());
        assertEquals(LocalDateTime.of(2022, 1, 1, 12, 0), p.getPostedOn());

        postDB.delete(p3);

        l = postDB.findAll();
        counter = 0;
        for (Post ignored : l)
            counter++;
        assertEquals(1, counter);

    }

    @Test
    public void serviceFriendshipDBTest() {

        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = new FriendshipDB();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        FriendshipService friendshipService = new FriendshipService(friendshipRepository, friendshipValidator);

        Repository<Long, User> userRepository = new UserDB();

        User u1 = new User("Stefan", "Farcasanu", "stefan4556", "P1");
        u1.setId(1L);

        User u2 = new User("Victor", "Doroftei", "vic", "P2");
        u2.setId(2L);

        User u3 = new User("Radu", "Nita", "foca", "P3");
        u3.setId(3L);

        User u4 = new User("Andrei", "Covaciu", "ake", "P4");
        u4.setId(4L);

        User u5 = new User("Alex", "Gheorghe", "mattix", "P5");
        u5.setId(5L);

        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);
        userRepository.save(u4);
        userRepository.save(u5);

        friendshipRepository.save(new Friendship(3L, 4L));
        friendshipRepository.save(new Friendship(1L, 5L));
        friendshipRepository.save(new Friendship(1L, 2L));

        Iterable<Friendship> friendships = friendshipService.getAll();
        int ct = 0;
        for (Friendship f : friendships)
            ct++;
        assertEquals(ct, 3);

        assertThrows(ValidationException.class, () -> friendshipService.addFriendship(new Friendship(1L, 1L)));
        assertThrows(ServiceException.class, () -> friendshipService.addFriendship(new Friendship(2L, 1L)));
        assertThrows(ServiceException.class, () -> friendshipService.deleteFriendship(new FriendshipDTO(new UserDTO(7L, "Fn", "Ln", "Un"), new UserDTO(6L, "Fn", "Ln", "Un"), LocalDateTime.now())));

        friendshipService.addFriendship(new Friendship(1L, 4L));
        friendships = friendshipService.getAll();
        ct = 0;
        for (Friendship f : friendships)
            ct++;
        assertEquals(ct, 4);

        friendshipService.deleteFriendship(new FriendshipDTO(new UserDTO(4L, "Fn", "Ln", "Un"), new UserDTO(1L, "Fn", "Ln", "Un"), LocalDateTime.now()));
        friendships = friendshipService.getAll();
        ct = 0;
        for (Friendship f : friendships)
            ct++;
        assertEquals(ct, 3);

        assertThrows(ValidationException.class, () -> friendshipService.updateFriendship(new FriendshipDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(1L, "Fn", "Ln", "Un"), LocalDateTime.parse("2020-08-09 19:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))));
        assertThrows(ServiceException.class, () -> friendshipService.updateFriendship(new FriendshipDTO(new UserDTO(7L, "Fn", "Ln", "Un"), new UserDTO(6L, "Fn", "Ln", "Un"), LocalDateTime.parse("2020-08-09 19:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))));

        String str = "2021-11-07 21:25";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime oldDateTime = LocalDateTime.parse(str, formatter);

        String str2 = "2010-11-07 21:25";
        LocalDateTime newDateTime = LocalDateTime.parse(str2, formatter);

        friendshipService.updateFriendship(new FriendshipDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(2L, "Fn", "Ln", "Un"), newDateTime));
        Friendship friendship = friendshipService.findOne(new FriendshipDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(2L, "Fn", "Ln", "Un"), LocalDateTime.now()));
        assertEquals(friendship.getDate(), newDateTime);

        friendshipService.updateFriendship(new FriendshipDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(2L, "Fn", "Ln", "Un"), oldDateTime));
        friendship = friendshipService.findOne(new FriendshipDTO(new UserDTO(2L, "Fn", "Ln", "Un"), new UserDTO(1L, "Fn", "Ln", "Un"), LocalDateTime.now()));
        assertEquals(friendship.getDate(), oldDateTime);
    }

    @Test
    public void serviceUserDBTest() {

        Repository<Long, User> userRepository = new UserDB();
        UserValidator userValidator = new UserValidator();
        UserService userService = new UserService(userRepository, userValidator);

        User u1 = new User("Stefan", "Farcasanu", "stefan4556", "P1");
        u1.setId(1L);

        User u2 = new User("Victor", "Doroftei", "vic", "P2");
        u2.setId(2L);

        User u3 = new User("Radu", "Nita", "foca", "P3");
        u3.setId(3L);

        User u4 = new User("Andrei", "Covaciu", "ake", "P4");
        u4.setId(4L);

        User u5 = new User("Alex", "Gheorghe", "mattix", "P5");
        u5.setId(5L);

        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);
        userRepository.save(u4);
        userRepository.save(u5);

        assertThrows(ValidationException.class, () -> userService.addUser(new User("", "", "", "P1")));
        assertThrows(ServiceException.class, () -> userService.addUser(new User("a", "a", "stefan4556", "P1")));

        Iterable<User> users = userService.getAll();
        int ct = 0;
        for (User u : users)
            ct++;
        assertEquals(ct, 5);

        assertThrows(ValidationException.class, () -> userService.deleteUser(new UserDTO(1L, "Fn", "Ln", "")));
        assertThrows(ServiceException.class, () -> userService.deleteUser(new UserDTO(1L, "Fn", "Ln", "a")));

        userService.addUser(new User("a", "a", "a", "P1"));
        ct = 0;
        users = userService.getAll();
        for (User u : users)
            ct++;
        assertEquals(ct, 6);

        userService.deleteUser(new UserDTO(1L, "Fn", "Ln", "a"));
        ct = 0;
        users = userService.getAll();
        for (User u : users)
            ct++;
        assertEquals(ct, 5);

        User user = userService.findOne(new UserDTO(1L, "Fn", "Ln", "Un"));
        assertEquals(user.getUserName(), "stefan4556");

        assertThrows(ValidationException.class, () -> userService.validateUsernameOrUsernames(""));
        assertThrows(ServiceException.class, () -> userService.validateUsernameOrUsernames("", ""));
        assertThrows(ServiceException.class, () -> userService.updateUser(new UserDTO(1L, "Fn", "Ln", ""), new UserDTO(2L, "a", "a", "a")));
        assertThrows(ValidationException.class, () -> userService.updateUser(new UserDTO(1L, "Fn", "Ln", "a"), new UserDTO(2L, "a", "a", "")));
        assertThrows(ServiceException.class, () -> userService.updateUser(new UserDTO(1L, "Fn", "Ln", "a"), new UserDTO(2L, "a", "a", "a")));

        LocalDateTime ldt1 = LocalDateTime.of(2021, 12, 17, 17, 40);
        userService.updateUser(new UserDTO(1L, "Fn", "Ln", "stefan4556", null), new UserDTO(2L, "test", "test", "test", ldt1));

        User u = userService.findOne(new UserDTO(1L, "Fn", "Ln", "Un"));
        assertEquals(u.getUserName(), "test");
        assertEquals(ldt1, u.getLastLogin());

        userService.updateUser(new UserDTO(1L, "Fn", "Ln", "test"), new UserDTO(2L, "Stefan", "Farcasanu", "stefan4556"));
        u = userService.findOne(new UserDTO(1L, "Fn", "Ln", "Un"));
        assertEquals(u.getUserName(), "stefan4556");
        assertNull(u.getLastLogin());
    }

    @Test
    public void serviceProfileDBTest() {

        Repository<Long, User> userRepository = new UserDB();
        UserValidator userValidator = new UserValidator();
        UserService userService = new UserService(userRepository, userValidator);

        Repository<Long, Profile> profileRepository = new ProfileDB();
        ProfileService profileService = new ProfileService(profileRepository);

        User u1 = new User("Stefan", "Farcasanu", "stefan4556", "P1");
        u1.setId(1L);

        User u2 = new User("Victor", "Doroftei", "vic", "P2");
        u2.setId(2L);

        userRepository.save(u1);
        userRepository.save(u2);

        Profile p1 = new Profile("I'm Stefan", "Rm. Valcea", LocalDate.of(2001, 8, 20), "Formula 1");
        p1.setId(1L);

        Profile p2 = new Profile("I'm Victor", "Suceava", LocalDate.of(2002, 4, 5), "Airplanes");
        p2.setId(2L);

        profileService.addProfile(p1);
        profileService.addProfile(p2);

        Iterable<Profile> it = profileRepository.findAll();
        int counter = 0;
        for (Profile ignored : it)
            counter++;

        assertEquals(2, counter);

        Profile profile1 = profileService.getProfileForUser(u1);
        Profile profile2 = profileService.getProfileForUser(u2);

        assertEquals(1L, profile1.getId());
        assertEquals("I'm Stefan", profile1.getAboutMe());
        assertEquals("Rm. Valcea", profile1.getHomeTown());
        assertEquals(LocalDate.of(2001, 8, 20), profile1.getBirthday());
        assertEquals("Formula 1", profile1.getHobbies());

        assertEquals(2L, profile2.getId());
        assertEquals("I'm Victor", profile2.getAboutMe());
        assertEquals("Suceava", profile2.getHomeTown());
        assertEquals(LocalDate.of(2002, 4, 5), profile2.getBirthday());
        assertEquals("Airplanes", profile2.getHobbies());

        Profile p3 = new Profile("Victor", "Iasi", LocalDate.of(2000, 2, 2), "Programming");
        p3.setId(2L);

        profileService.updateProfile(p3);

        profile2 = profileService.getProfileForUser(u2);

        assertEquals(2L, profile2.getId());
        assertEquals("Victor", profile2.getAboutMe());
        assertEquals("Iasi", profile2.getHomeTown());
        assertEquals(LocalDate.of(2000, 2, 2), profile2.getBirthday());
        assertEquals("Programming", profile2.getHobbies());

        profileService.deleteProfile(profile1);

        it = profileRepository.findAll();
        counter = 0;
        for (Profile ignored : it)
            counter++;

        assertEquals(1, counter);

        userService.deleteUser(new UserDTO(u2.getId(), u2.getFirstName(), u2.getLastName(), u2.getUserName()));

    }

    @Test
    public void servicePostDBTest() {

        Repository<Long, User> userRepository = new UserDB();

        Repository<Long, Post> postRepository = new PostDB();
        PostService postService = new PostService(postRepository);

        User u1 = new User("Stefan", "Farcasanu", "stefan4556", "P1");
        u1.setId(1L);

        User u2 = new User("Victor", "Doroftei", "vic", "P2");
        u2.setId(2L);

        userRepository.save(u1);
        userRepository.save(u2);

        Post p1 = new Post(1L, "hai in mercedes", LocalDateTime.of(2020, 1, 1, 12, 0));
        p1.setId(1L);

        Post p2 = new Post(2L, "hai in avion", LocalDateTime.of(2021, 1, 1, 12, 0));
        p2.setId(2L);

        postService.addPost(p1);
        postService.addPost(p2);

        Iterable<Post> it = postRepository.findAll();
        int counter = 0;
        for (Post ignored : it)
            counter++;
        assertEquals(2, counter);

        Post p3 = new Post(2L, "Good morning everyone", LocalDateTime.of(2022, 1, 1, 12, 0));
        p3.setId(3L);

        postService.addPost(p3);

        UserDTO userDTO1 = new UserDTO(u1.getId(), u1.getFirstName(), u1.getLastName(), u1.getUserName());
        UserDTO userDTO2 = new UserDTO(u2.getId(), u2.getFirstName(), u2.getLastName(), u2.getUserName());

        List<Post> posts = postService.getAllPostsForUser(userDTO1);
        assertEquals(1, posts.size());
        assertEquals(1L, posts.get(0).getId());
        assertEquals("hai in mercedes", posts.get(0).getContent());

        posts = postService.getAllPostsForUser(userDTO2);
        assertEquals(2, posts.size());

        assertEquals(3L, posts.get(0).getId());
        assertEquals(2L, posts.get(1).getId());
        assertEquals("Good morning everyone", posts.get(0).getContent());
        assertEquals("hai in avion", posts.get(1).getContent());

        Post p4 = new Post(2L, "Good evening everyone", LocalDateTime.of(2023, 1, 1, 20, 0));
        p4.setId(3L);
        postService.updatePost(p4);

        assertEquals(3L, p4.getId());
        assertEquals(2L, p4.getIdUser());
        assertEquals("Good evening everyone", p4.getContent());
        assertEquals(LocalDateTime.of(2023, 1, 1, 20, 0), p4.getPostedOn());

        postService.deletePost(p1);
        posts = postService.getAllPostsForUser(userDTO1);
        assertEquals(0, posts.size());

        postService.deletePost(p3);
        posts = postService.getAllPostsForUser(userDTO2);
        assertEquals(1, posts.size());
        assertEquals(2L, posts.get(0).getId());
        assertEquals("hai in avion", posts.get(0).getContent());

    }

    @Test
    public void requestTest() {

        Request request1 = new Request(1L, 1L, 2L, "pending");

        assertEquals(request1.getId(), 1L);
        assertEquals(request1.getIdUser1(), 1L);
        assertEquals(request1.getIdUser2(), 2L);
        assertEquals(request1.getStatus(), Status.getBySymbol("pending"));

        request1.setId(2L);
        request1.setIdUser1(2L);
        request1.setIdUser2(1L);
        request1.setStatus(Status.getBySymbol("rejected"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime date = LocalDateTime.parse("2021-10-10 10:10", formatter);
        request1.setDate(date);

        Request request2 = new Request(1L, 1L, 2L, date, "pending");

        assertEquals(request2.getId(), 1L);
        assertEquals(request2.getIdUser1(), 1L);
        assertEquals(request2.getIdUser2(), 2L);
        assertEquals(request2.getStatus(), Status.getBySymbol("pending"));
        assertNull(Status.getBySymbol("dadada"));
        assertEquals(request2.getStatus().getStatus(), "pending");
        assertEquals(request2.getDate(), date);

        assertEquals(request1.getId(), 2L);
        assertEquals(request1.getIdUser1(), 2L);
        assertEquals(request1.getIdUser2(), 1L);
        assertEquals(request1.getStatus(), Status.getBySymbol("rejected"));
        assertEquals(request1.getDate(), date);

        Validator<Request> requestValidator = new RequestValidator();

        assertThrows(ValidationException.class, () -> requestValidator.validate(new Request(1L, 1L, 1L, "rejected")));
        assertThrows(ValidationException.class, () -> requestValidator.validate(new Request(1L, null, null, "da")));
    }

    @Test
    public void serviceRequestDBTest() {

        PagingRepository<Long, Request> requestRepository = new RequestDB();
        RequestValidator requestValidator = new RequestValidator();
        RequestService requestService = new RequestService(requestRepository, requestValidator);

        Repository<Long, User> userRepository = new UserDB();

        User u1 = new User("Stefan", "Farcasanu", "stefan4556", "P1");
        u1.setId(1L);

        User u2 = new User("Victor", "Doroftei", "vic", "P2");
        u2.setId(2L);

        User u3 = new User("Radu", "Nita", "foca", "P3");
        u3.setId(3L);

        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);

        requestService.addRequest(new RequestDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(2L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending"));

        assertThrows(ServiceException.class, () -> requestService.addRequest(new RequestDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(2L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending")));
        assertThrows(ServiceException.class, () -> requestService.addRequest(new RequestDTO(new UserDTO(2L, "Fn", "Ln", "Un"), new UserDTO(1L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending")));

        Iterable<Request> requests = requestService.getAll();

        int ct = 0;

        for (Request r : requests)

            ct++;

        assertEquals(ct, 1);

        assertThrows(ServiceException.class, () -> requestService.addRequest(new RequestDTO(new UserDTO(2L, "Fn", "Ln", "Un"), new UserDTO(1L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending")));
        assertThrows(ServiceException.class, () -> requestService.deleteRequest(new RequestDTO(new UserDTO(2L, "Fn", "Ln", "Un"), new UserDTO(4L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending")));

        requestService.deleteRequest(new RequestDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(2L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending"));

        ct = 0;

        for (Request r : requestService.getAll())

            ct++;

        assertEquals(ct, 0);

        requestService.addRequest(new RequestDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(2L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending"));
        requestService.addRequest(new RequestDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(3L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending"));

        Iterable<Request> requestIterable = requestService.getAllRequestsForAUser(new UserDTO(1L, "Fn", "Ln", "Un"));

        for (Request r : requestIterable)

            ct++;

        assertEquals(ct, 2);

        assertThrows(ServiceException.class, () -> requestService.acceptRequest(new RequestDTO(new UserDTO(3L, "Fn", "Ln", "Un"), new UserDTO(4L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending")));
        assertThrows(ValidationException.class, () -> requestService.acceptRequest(new RequestDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(1L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending")));
        assertThrows(ServiceException.class, () -> requestService.acceptRequest(new RequestDTO(new UserDTO(2L, "Fn", "Ln", "Un"), new UserDTO(1L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending")));

        requestService.acceptRequest(new RequestDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(2L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending"));
        ct = 0;
        requestIterable = requestService.getAll();
        for (Request r : requestIterable)
            ct++;
        assertEquals(ct, 1);

        requestService.acceptRequest(new RequestDTO(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(3L, "Fn", "Ln", "Un"), LocalDateTime.now(), "pending"));
        ct = 0;
        for (Request r : requestService.getAll())
            ct++;
        assertEquals(ct, 0);
    }

    @Test
    public void serviceMessageDBTest() {

        PagingRepository<Long, Message> repo = new MessageDB();
        MessageValidator validator = new MessageValidator();
        MessageService messageService = new MessageService(repo, validator);

        Repository<Long, User> userRepository = new UserDB();

        User u1 = new User("a", "b", "ab", null);
        u1.setId(1L);

        userRepository.save(u1);

        User u2 = new User("a", "b", "cd", null);
        u2.setId(2L);
        userRepository.save(u2);

        u1 = new User("a", "b", "ef", null);
        u1.setId(3L);

        userRepository.save(u1);

        u2 = new User("a", "b", "gh", null);
        u2.setId(4L);
        userRepository.save(u2);

        assertThrows(ValidationException.class, () -> messageService.addMessage(new MessageDTO(1l, new UserDTO(2L, "Fn", "Ln", "Un"), List.of(), "ccc", LocalDateTime.now(), null)));

        //2L, List.of(3L, 1L), "servus"
        messageService.addMessage(new MessageDTO(1L, new UserDTO(2L, "Fn", "Ln", "Un"), List.of(new UserDTO(3L, "Fn", "Ln", "Un"), new UserDTO(1L, "Fn", "Ln", "Un")), "servus", LocalDateTime.now(), null));

        List<Message> messages = messageService.getMessagesForAUser(new UserDTO(2L, "Fn", "Ln", "Un"));
        assertEquals(messages.size(), 1);

        assertEquals(messages.get(0).getId(), 1L);

        assertEquals(messages.get(0).getFrom(), 2L);

        assertEquals(messages.get(0).getTo().size(), 2);

        assertTrue(messages.get(0).getTo().contains(3L));
        assertTrue(messages.get(0).getTo().contains(1L));

        assertEquals(messages.get(0).getMessage(), "servus");

        //assert (messages.getReply() == null);

        repo.delete(new Message(1L, null, null, null));

        messageService.addMessage(new MessageDTO(1L, new UserDTO(1L, "Fn", "Ln", "Un"), List.of(new UserDTO(2L, "Fn", "Ln", "Un"), new UserDTO(3L, "Fn", "Ln", "Un")), "salutare", LocalDateTime.now(), null));

        messageService.replyToMessage(new MessageDTO(null, new UserDTO(2L, "X", "X", "vic"), List.of(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(3L, "Fn", "Ln", "Un")), "servus draga", LocalDateTime.now(), 1L));

        repo.delete(new Message(1L, null, null, null));
        repo.delete(new Message(2L, null, null, null));

        messageService.addMessage(new MessageDTO(1L, new UserDTO(1L, "Fn", "Ln", "Un"), List.of(new UserDTO(2L, "Fn", "Ln", "Un"), new UserDTO(3L, "Fn", "Ln", "Un"), new UserDTO(4L, "Fn", "Ln", "Un")), "Buna ziua bat pari", LocalDateTime.now(), null));

        messageService.replyToAll(new MessageDTO(null, new UserDTO(2L, null, null, "vic"), List.of(new UserDTO(1L, "Fn", "Ln", "Un"), new UserDTO(3L, "Fn", "Ln", "Un"), new UserDTO(4L, "Fn", "Ln", "Un")), "Servus bat pari si eu", LocalDateTime.now(), 1L));

        List<Message> messages1 = new ArrayList<>();
        repo.findAll().forEach(messages1::add);

        Message message = repo.findOne(2L);

        assertEquals(messages1.size(), 2);

        assertEquals(message.getFrom(), 2L);

        assertEquals(message.getOriginalMessage(), 1L);

        assertEquals(message.getTo().size(), 3);
        assertTrue(message.getTo().contains(1L));
        assertTrue(message.getTo().contains(3L));
        assertTrue(message.getTo().contains(4L));

        repo.delete(new Message(1L, null, null, null));
        repo.delete(new Message(2L, null, null, null));

    }

    @Test
    public void md5Test() {

        String password1 = "P1";
        assert (MD5.getMD5(password1).equals("5f2b9323c39ee3c861a7b382d205c3d3"));

        assert (MD5.getMD5("P2").equals("5890595e16cbebb8866e1842e4bd6ec7"));
    }

    @Test
    public void superServiceDBTest() {

        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = new FriendshipDB();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        FriendshipService friendshipService = new FriendshipService(friendshipRepository, friendshipValidator);

        Repository<Long, User> userRepository = new UserDB();
        UserValidator userValidator = new UserValidator();
        UserService userService = new UserService(userRepository, userValidator);

        PagingRepository<Long, Request> requestRepository = new RequestDB();
        RequestValidator requestValidator = new RequestValidator();
        RequestService requestService = new RequestService(requestRepository, requestValidator);

        PagingRepository<Long, Message> messageRepository = new MessageDB();
        MessageValidator messageValidator = new MessageValidator();
        MessageService messageService = new MessageService(messageRepository, messageValidator);

        PagingRepository<Long, Event> eventRepository = new EventDB();
        EventParticipantsDB eventParticipantsRepository = new EventParticipantsDB();
        EventValidator eventValidator = new EventValidator();
        EventService eventService = new EventService(eventRepository, eventParticipantsRepository, eventValidator);

        Repository<Long, Profile> profileRepository = new ProfileDB();
        ProfileService profileService = new ProfileService(profileRepository);

        Repository<Long, Post> postRepository = new PostDB();
        PostService postService = new PostService(postRepository);

        User usr1 = new User("Stefan", "Farcasanu", "stefan4556", "P1");
        usr1.setId(1L);

        User usr2 = new User("Victor", "Doroftei", "vic", "P2");
        usr2.setId(2L);

        User usr3 = new User("Radu", "Nita", "foca", "P3");
        usr3.setId(3L);

        User usr4 = new User("Andrei", "Covaciu", "ake", "P4");
        usr4.setId(4L);

        User usr5 = new User("Alex", "Gheorghe", "mattix", "P5");
        usr5.setId(5L);

        userRepository.save(usr1);
        userRepository.save(usr2);
        userRepository.save(usr3);
        userRepository.save(usr4);
        userRepository.save(usr5);

        SuperService superService = new SuperService(userService, friendshipService, requestService, messageService, eventService, profileService, postService);

        friendshipRepository.save(new Friendship(3L, 4L));
        friendshipRepository.save(new Friendship(1L, 5L));
        friendshipRepository.save(new Friendship(1L, 2L));

        UserRegisterDTO u6 = new UserRegisterDTO(new UserDTO(6L, "uu", "uu", "u6"), "u6");
        UserRegisterDTO u7 = new UserRegisterDTO(new UserDTO(7L, "uu", "uu", "u7"), "u7");
        superService.addUser(u6);
        superService.addUser(u7);

        superService.addRequest(new RequestDTO(new UserDTO(6L, "uu", "uu", "u6"), new UserDTO(7L, "uu", "uu", "u7"), LocalDateTime.now(), "pending"));

        superService.updateUser(new UserDTO(6L, "uu", "uu", "u6"), new UserDTO(6L, "uu", "uu", "u6", LocalDateTime.of(2020, 10, 10, 10, 10)));

        assertEquals(1, superService.getAllRecentRequests(new UserDTO(6L, "uu", "uu", "u6", LocalDateTime.of(2020, 10, 10, 10, 10))).size());

        superService.deleteRequest(new RequestDTO(new UserDTO(6L, "uu", "uu", "u6"), new UserDTO(7L, "uu", "uu", "u7"), LocalDateTime.now(), "pending"));

        superService.addFriendship(new FriendshipDTO(new UserDTO(6L, "uu", "uu", "u6"), new UserDTO(7L, "uu", "uu", "u7"), LocalDateTime.now()));

        superService.addMessage(new MessageDTO(1L, new UserDTO(7L, "uu", "uu", "u7"), List.of(new UserDTO(6L, "uu", "uu", "u6")), "Salut", LocalDateTime.now(), null));

        assertEquals(1, superService.getAllRecentMessages(new UserDTO(6L, "uu", "uu", "u6", LocalDateTime.of(2020, 10, 10, 10, 10))).size());

        messageRepository.delete(new Message(1L, null, null, null));
        superService.removeUser(new UserDTO(6L, "uu", "uu", "u6"));
        superService.removeUser(new UserDTO(7L, "uu", "uu", "u7"));

        Iterable<User> userIterable = superService.getAll();
        int ctUser = 0;
        for (User u : userIterable)
            ctUser++;
        assertEquals(ctUser, 5);

        assertEquals(superService.getAllFriendships().size(), 3);

        superService.addUser(new UserRegisterDTO(new UserDTO(1L, "a", "a", "a"), "P1"));
        userIterable = superService.getAll();
        ctUser = 0;
        for (User u : userIterable)
            ctUser++;
        assertEquals(ctUser, 6);

        assertThrows(ServiceException.class, () -> superService.loginUserUsingUsername(new UserRegisterDTO(new UserDTO(1L, "Aa", "Bb", "jghjgh"), "P5")));
        assertThrows(ServiceException.class, () -> superService.loginUserUsingUsername(new UserRegisterDTO(new UserDTO(1L, "Aa", "Bb", "a"), "asdasdsad")));

        assertEquals(superService.loginUserUsingUsername(new UserRegisterDTO(new UserDTO(1L, "Aa", "Bb", "a"), "P1")), 6L);

        superService.removeUser(new UserDTO(1L, "x", "x", "a"));
        userIterable = superService.getAll();
        ctUser = 0;
        for (User u : userIterable)
            ctUser++;
        assertEquals(ctUser, 5);

        superService.addFriendship(new FriendshipDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now()));
        assertEquals(superService.getAllFriendships().size(), 4);

        superService.removeFriendship(new FriendshipDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now()));
        assertEquals(superService.getAllFriendships().size(), 3);

        assertThrows(ServiceException.class, () -> superService.addFriendship(new FriendshipDTO(new UserDTO(2L, "x", "x", "invalid"), new UserDTO(3L, "x", "x", "invalid"), LocalDateTime.now())));
        assertThrows(ServiceException.class, () -> superService.addFriendship(new FriendshipDTO(new UserDTO(2L, "Victor", "Doroftei", "vic"), new UserDTO(3L, "x", "x", "invalid"), LocalDateTime.now())));
        assertThrows(ServiceException.class, () -> superService.addFriendship(new FriendshipDTO(new UserDTO(0L, "x", "x", "invalid"), new UserDTO(-1L, "x", "x", "vic"), LocalDateTime.now())));

        assertNull(superService.updateUser(new UserDTO(1L, "Stefan", "Farcasanu", "stefan4556"), new UserDTO(0L, "a", "a", "a")));
        assertEquals(userRepository.findOne(1L).getUserName(), "a");

        assertNull(superService.updateUser(new UserDTO(0L, "a", "a", "a"), new UserDTO(1L, "Stefan", "Farcasanu", "stefan4556")));
        assertEquals(userRepository.findOne(1L).getUserName(), "stefan4556");

        String oldDate = "2021-11-07 21:25";
        String newDate = "2010-11-07 21:25";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime oldDateTime = LocalDateTime.parse(oldDate, formatter);
        LocalDateTime newDateTime = LocalDateTime.parse(newDate, formatter);

        superService.updateFriendship(new FriendshipDTO(new UserDTO(1L, "x", "x", "stefan4556"), new UserDTO(2L, "x", "x", "vic"), newDateTime));
        assertEquals(friendshipRepository.findOne(new Tuple<>(1L, 2L)).getDate(), newDateTime);

        superService.updateFriendship(new FriendshipDTO(new UserDTO(1L, "x", "x", "stefan4556"), new UserDTO(2L, "x", "x", "vic"), oldDateTime));
        assertEquals(friendshipRepository.findOne(new Tuple<>(1L, 2L)).getDate(), oldDateTime);

        assertEquals(superService.getNumberOfConnectedComponents(), 2);
        assertEquals(superService.getTheMostSociableConnection().size(), 3);
        assertEquals(superService.getAllConnections().size(), 2);

        // Testing getAllFriendshipsForAUser

        assertThrows(ValidationException.class, () -> superService.getAllFriendshipsForAUser(new UserDTO(0L, "x", "x", "")));
        assertThrows(ServiceException.class, () -> superService.getAllFriendshipsForAUser(new UserDTO(0L, "x", "x", "test")));

        List<FriendshipDTO> friendshipDTOList = superService.getAllFriendshipsForAUser(new UserDTO(0L, "x", "x", "stefan4556"));
        assertEquals(friendshipDTOList.size(), 2);

        String fn1 = friendshipDTOList.get(0).getUser2().getFirstName(), fn2 = friendshipDTOList.get(1).getUser2().getFirstName();

        assertTrue((fn1.equals("Victor") && fn2.equals("Alex")) || (fn1.equals("Alex") && fn2.equals("Victor")));

        assertEquals(superService.getAllFriendshipsForAUser(new UserDTO(0L, "x", "x", "ake")).size(), 1);

        superService.addUser(new UserRegisterDTO(new UserDTO(1L, "cucu", "mucu", "cumu"), "p1"));

        superService.removeUser(new UserDTO(1L, "x", "x", "cumu"));

        // Tests for getFriendshipsFromMonth method from SuperService class.

        superService.addUser(new UserRegisterDTO(new UserDTO(6L, "Cami", "Serbi", "camserb"), "p1"));
        superService.addUser(new UserRegisterDTO(new UserDTO(7L, "Flori", "Boti", "flobo"), "p1"));

        superService.addFriendship(new FriendshipDTO(new UserDTO(1L, "x", "x", "flobo"), new UserDTO(2L, "x", "x", "vic"), LocalDateTime.now()));
        superService.addFriendship(new FriendshipDTO(new UserDTO(1L, "x", "x", "camserb"), new UserDTO(2L, "x", "x", "flobo"), LocalDateTime.now()));
        superService.addFriendship(new FriendshipDTO(new UserDTO(1L, "x", "x", "flobo"), new UserDTO(2L, "x", "x", "foca"), LocalDateTime.now()));

        newDate = "2020-08-05 22:18";
        superService.updateFriendship(new FriendshipDTO(new UserDTO(7L, "x", "x", "flobo"), new UserDTO(6L, "x", "x", "camserb"), LocalDateTime.parse(newDate, formatter)));

        newDate = "2020-08-11 07:55";
        superService.updateFriendship(new FriendshipDTO(new UserDTO(7L, "x", "x", "flobo"), new UserDTO(2L, "x", "x", "vic"), LocalDateTime.parse(newDate, formatter)));

        List<FriendshipDTO> friendshipDTOs = superService.getFriendshipsFromMonth(new UserDTO(7L, "x", "x", "flobo"), "AUGUST");

        assertEquals(friendshipDTOs.size(), 2);

        assertEquals(friendshipDTOs.get(0).getUser1().getUserName(), "flobo");
        assertEquals(friendshipDTOs.get(1).getUser1().getUserName(), "flobo");

        assertEquals(friendshipDTOs.get(0).getUser2().getUserName(), "vic");
        assertEquals(friendshipDTOs.get(1).getUser2().getUserName(), "camserb");

        String newDate2 = "2020-12-01 11:11";
        String month = LocalDateTime.now().getMonth().toString();
        superService.updateFriendship(new FriendshipDTO(new UserDTO(7L, "x", "x", "flobo"), new UserDTO(6L, "x", "x", "foca"), newDateTime));
        friendshipDTOs = superService.getFriendshipsFromMonth(new UserDTO(7L, "x", "x", "flobo"), month);

        assertEquals(friendshipDTOs.size(), 1);

        assertEquals(friendshipDTOs.get(0).getUser1().getUserName(), "flobo");
        assertEquals(friendshipDTOs.get(0).getUser2().getUserName(), "foca");

        assertThrows(ServiceException.class, () -> superService.getFriendshipsFromMonth(new UserDTO(2L, "x", "x", "vic"), "january"));
        assertThrows(ServiceException.class, () -> superService.getFriendshipsFromMonth(new UserDTO(7L, "x", "x", "flobo"), "invalid"));
        assertThrows(ServiceException.class, () -> superService.getFriendshipsFromMonth(new UserDTO(0L, "x", "x", "invalid"), "October"));

        superService.removeUser(new UserDTO(1L, "x", "x", "flobo"));
        superService.removeUser(new UserDTO(2L, "x", "x", "camserb"));

        assertThrows(ServiceException.class, () -> superService.addRequest(new RequestDTO(new UserDTO(0L, "x", "x", "invalid"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending")));
        assertThrows(ServiceException.class, () -> superService.addRequest(new RequestDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(0L, "x", "x", "invalid"), LocalDateTime.now(), "pending")));
        assertThrows(ServiceException.class, () -> superService.addRequest(new RequestDTO(new UserDTO(0L, "x", "x", "invalid"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending")));
        assertThrows(ServiceException.class, () -> superService.addRequest(new RequestDTO(new UserDTO(0L, "x", "x", ""), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending")));
        assertThrows(ServiceException.class, () -> superService.addRequest(new RequestDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(0L, "x", "x", ""), LocalDateTime.now(), "pending")));
        assertThrows(ServiceException.class, () -> superService.addRequest(new RequestDTO(new UserDTO(0L, "x", "x", ""), new UserDTO(-1L, "x", "x", ""), LocalDateTime.now(), "pending")));

        // Tests for addRequest method in SuperService class.

        superService.addRequest(new RequestDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending"));

        List<RequestDTO> list = superService.getAllRequests();

        assertEquals(list.size(), 1);

        assertEquals(list.get(0).getUser1().getUserName(), "vic");
        assertEquals(list.get(0).getUser2().getUserName(), "foca");

        assertEquals(superService.getAllRequestsForAUser(new UserDTO(2L, "x", "x", "vic"), -1).size(), 1);
        assertEquals(superService.getAllRequestsForAUser(new UserDTO(4L, "x", "x", "ake"), -1).size(), 0);

        int size = superService.getAllFriendships().size();

        assertThrows(ServiceException.class, () -> superService.acceptRequest(new RequestDTO(new UserDTO(3L, "x", "x", "foca"), new UserDTO(2L, "x", "x", "vic"), LocalDateTime.now(), "pending")));

        superService.acceptRequest(new RequestDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending"));
        assertEquals(superService.getAllRequests().size(), 0);

        assertEquals(superService.getAllFriendships().size(), size + 1);
        assertNotNull(friendshipService.findOne(new FriendshipDTO(new UserDTO(2L, "Fn", "Ln", "Un"), new UserDTO(3L, "Fn", "Ln", "Un"), LocalDateTime.now())));

        assertThrows(ServiceException.class, () -> superService.acceptRequest(new RequestDTO(new UserDTO(1L, "x", "x", "stefan4556"), new UserDTO(4L, "x", "x", "ake"), LocalDateTime.now(), "pending")));

        superService.removeFriendship(new FriendshipDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now()));
        assertEquals(superService.getAllFriendships().size(), size);

        superService.addRequest(new RequestDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending"));
        assertEquals(superService.getAllRequests().size(), 1);

        assertThrows(ServiceException.class, () -> superService.rejectRequest(new RequestDTO(new UserDTO(1L, "x", "x", "stefan4556"), new UserDTO(4L, "x", "x", "ake"), LocalDateTime.now(), "pending")));

        superService.rejectRequest(new RequestDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending"));
        assertEquals(superService.getAllRequests().size(), 0);
        assertEquals(superService.getAllFriendships().size(), size);

        assertThrows(ServiceException.class, () -> superService.getAllRequestsForAUser(new UserDTO(0L, "x", "x", "invalid"), -1));
        assertThrows(ServiceException.class, () -> superService.addRequest(new RequestDTO(new UserDTO(4L, "x", "x", "ake"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending")));

        superService.addRequest(new RequestDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending"));

        superService.deleteRequest(new RequestDTO(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending"));

        list = superService.getAllRequests();

        assertEquals(list.size(), 0);

        assertThrows(ServiceException.class, () -> superService.addRequest(new RequestDTO(new UserDTO(4L, "x", "x", "ake"), new UserDTO(3L, "x", "x", "foca"), LocalDateTime.now(), "pending")));
        assertThrows(ValidationException.class, () -> superService.addMessage(new MessageDTO(1L, new UserDTO(2L, "x", "x", "vic"), List.of(new UserDTO(3L, "x", "x", "foca")), "", LocalDateTime.now(), null)));
        assertThrows(ServiceException.class, () -> superService.addMessage(new MessageDTO(1L, new UserDTO(0L, "x", "x", ""), List.of(new UserDTO(2L, "x", "x", "vic")), "aaa", LocalDateTime.now(), null)));
        assertThrows(ServiceException.class, () -> superService.addMessage(new MessageDTO(1L, new UserDTO(2L, "x", "x", "vic"), List.of(new UserDTO(0L, "x", "x", "")), "aaa", LocalDateTime.now(), null)));
        assertThrows(ServiceException.class, () -> superService.addMessage(new MessageDTO(1L, new UserDTO(0L, "x", "x", "invalid"), List.of(new UserDTO(2L, "x", "x", "vic")), "aaa", LocalDateTime.now(), null)));
        assertThrows(ServiceException.class, () -> superService.addMessage(new MessageDTO(1L, new UserDTO(2L, "x", "x", "vic"), List.of(new UserDTO(0L, "x", "x", "invalid")), "aaa", LocalDateTime.now(), null)));
        assertThrows(ValidationException.class, () -> superService.addMessage(new MessageDTO(1L, new UserDTO(2L, "x", "x", "vic"), List.of(), "aaa", LocalDateTime.now(), null)));
        assertThrows(ServiceException.class, () -> superService.addMessage(new MessageDTO(1L, new UserDTO(2L, "x", "x", "vic"), List.of(new UserDTO(2L, "x", "x", "vic")), "aaa", LocalDateTime.now(), null)));
        assertThrows(ServiceException.class, () -> superService.addMessage(new MessageDTO(1L, new UserDTO(2L, "x", "x", "vic"), List.of(new UserDTO(3L, "x", "x", "foca"), new UserDTO(0L, "x", "x", "invalid"), new UserDTO(4L, "x", "x", "ake")), "aaa", LocalDateTime.now(), null)));


        Iterable<Message> messages = messageService.getAll();

        List<Message> messageList = new ArrayList<Message>();
        for (Message message : messages)
            messageList.add(message);

        assertEquals(messageList.size(), 1);

        assertEquals(messageList.get(0).getId(), 1L);

        assertEquals(messageList.get(0).getFrom(), 2L);

        assertEquals(messageList.get(0).getTo().size(), 2);
        assertTrue(messageList.get(0).getTo().contains(3L));
        assertTrue(messageList.get(0).getTo().contains(4L));

        assertEquals(messageList.get(0).getMessage(), "aaa");

        //assert (messageList.getReply() == null);

        superService.addMessage(new MessageDTO(1L, new UserDTO(3L, "x", "x", "foca"), List.of(new UserDTO(2L, "x", "x", "vic")), "sal", LocalDateTime.now(), null));

        messageList.clear();
        assertEquals(superService.getConversations(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca")).size(), 1);
        assertEquals(superService.getConversations(new UserDTO(3L, "x", "x", "foca"), new UserDTO(2L, "x", "x", "vic")).size(), 1);

        assertThrows(ServiceException.class, () -> superService.getConversations(new UserDTO(0L, "x", "x", ""), new UserDTO(2L, "x", "x", "vic")));
        assertThrows(ServiceException.class, () -> superService.getConversations(new UserDTO(2L, "x", "x", "vic"), new UserDTO(0L, "x", "x", "")));
        assertThrows(ServiceException.class, () -> superService.getConversations(new UserDTO(0L, "x", "x", "invalid"), new UserDTO(3L, "x", "x", "vic")));
        assertThrows(ServiceException.class, () -> superService.getConversations(new UserDTO(2L, "x", "x", "vic"), new UserDTO(0L, "x", "x", "invalid")));

        messageRepository.delete(new Message(1L, null, null, null));
        messageRepository.delete(new Message(2L, null, null, null));

        messages = messageService.getAll();

        messageList = new ArrayList<Message>();
        for (Message message : messages)
            messageList.add(message);

        assertEquals(messageList.size(), 0);

        superService.addMessage(new MessageDTO(2L, new UserDTO(1L, "x", "x", "stefan4556"), List.of(new UserDTO(2L, "x", "x", "vic"), new UserDTO(3L, "x", "x", "foca")), "mesaj", LocalDateTime.now(), null));

        superService.replyToMessage(new MessageDTO(null, new UserDTO(2L, "x", "x", "vic"), List.of(new UserDTO(1L, "x", "x", "stefan4556"), new UserDTO(3L, "x", "x", "foca")), "mesaj2", LocalDateTime.now(), 1L));

        assertEquals(superService.getAllMessagesForAUser(new UserDTO(1L, "x", "x", "stefan4556"), 0).size(), 2);

        messageRepository.delete(new Message(1L, null, null, null));
        messageRepository.delete(new Message(2L, null, null, null));

        assertThrows(ServiceException.class, () -> superService.getAllMessagesForAUser(new UserDTO(0L, "x", "x", "servus"), 0));

        superService.addMessage(new MessageDTO(2L, new UserDTO(2L, "x", "x", "vic"), List.of(new UserDTO(1L, "x", "x", "stefan4556"), new UserDTO(4L, "x", "x", "ake"), new UserDTO(3L, "x", "x", "foca")), "Alabala", LocalDateTime.now(), null));

        superService.replyToAll(new MessageDTO(null, new UserDTO(3L, null, null, "foca"), List.of(new UserDTO(1L, "x", "x", "stefan4556"), new UserDTO(4L, "x", "x", "ake"), new UserDTO(2L, "x", "x", "vic")), "Bunasiuuuuaa", LocalDateTime.now(), 1L));

        Message message = messageRepository.findOne(2L);

        assertEquals(message.getTo().size(), 3);
        assertTrue(message.getTo().contains(1L));
        assertTrue(message.getTo().contains(2L));
        assertTrue(message.getTo().contains(4L));

        messageRepository.delete(new Message(1L, null, null, null));
        messageRepository.delete(new Message(2L, null, null, null));

        List<UserDTO> udl = new ArrayList<>();
        UserDTO u1 = new UserDTO(0L, "Andrei", "Mihai", "andimi");
        UserDTO u2 = new UserDTO(0L, "Mihaela", "Petrescu", "mipe");
        udl.add(u1);
        udl.add(u2);

        List<UserDTO> result = superService.filterUsersByName(udl, "re");
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), u1);
        assertEquals(result.get(1), u2);

        result = superService.filterUsersByName(udl, "re");
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), u1);
        assertEquals(result.get(1), u2);

        result = superService.filterUsersByName(udl, "and");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), u1);

        result = superService.filterUsersByName(udl, "pet");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), u2);

        result = superService.filterUsersByName(udl, "pEt");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), u2);

        result = superService.filterUsersByName(udl, "AND");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), u1);

        superService.addMessage(new MessageDTO(1L, new UserDTO(1L, "x", "x", "stefan4556"), List.of(new UserDTO(2L, "x", "x", "vic")), "salut", LocalDateTime.now(), null));
        superService.addMessage(new MessageDTO(2L, new UserDTO(1L, "x", "x", "stefan4556"), List.of(new UserDTO(3L, "x", "x", "foca"), new UserDTO(4L, "x", "x", "ake")), "salut grup", LocalDateTime.now(), null));
        superService.addMessage(new MessageDTO(3L, new UserDTO(2L, "x", "x", "vic"), List.of(new UserDTO(1L, "x", "x", "stefan4556")), "hello", LocalDateTime.now(), 1L));
        superService.addMessage(new MessageDTO(4L, new UserDTO(2L, "x", "x", "vic"), List.of(new UserDTO(3L, "x", "x", "foca")), "hello foca", LocalDateTime.now(), null));

        Map<Integer, List<MessageDTO>> map = superService.getAllConversations(new UserDTO(1L, "x", "x", "stefan4556"));

        assertEquals(map.size(), 2);

        superService.addRequest(new RequestDTO(new UserDTO(1L, null, null, "stefan4556"), new UserDTO(3L, null, null, "foca"), LocalDateTime.now(), "pending"));

        superService.updateUser(new UserDTO(1L, "x", "x", "stefan4556"), new UserDTO(1L, "x", "x", "stefan4556", LocalDateTime.of(2019, 10, 10, 10, 10)));

        Page page = superService.getPageForUser(new UserDTO(1L, null, null, "stefan4556"));

        assertEquals(page.getUser().getId(), 1L);
        assertEquals(page.getUser().getFirstName(), "x");
        assertEquals("x", page.getUser().getLastName());
        assertEquals("stefan4556", page.getUser().getUserName());

        assertEquals(2, page.getFriendsList().size());

        assertEquals(1, page.getMessages().size());

        assertEquals(1, page.getRequests().size());

        EventDTO ev1 = new EventDTO(null, "Oktoberfest", new UserDTO(4L, null, null, null), "Beer festival", LocalDateTime.of(2030, 10, 5, 18, 0), null);
        EventDTO ev2 = new EventDTO(null, "Pastele Cailor", new UserDTO(2L, null, null, null), "Fictiune", LocalDateTime.of(2277, 12, 30, 22, 35), null);

        superService.addEvent(ev1);
        superService.addEvent(ev2);

        ev1.setId(1L);
        ev2.setId(2L);

        superService.addParticipant(ev1, new UserDTO(1L, null, null, null));
        superService.addParticipant(ev1, new UserDTO(2L, null, null, null));
        superService.addParticipant(ev1, new UserDTO(3L, null, null, null));

        superService.addParticipant(ev2, new UserDTO(1L, null, null, null));
        superService.addParticipant(ev2, new UserDTO(4L, null, null, null));
        superService.addParticipant(ev2, new UserDTO(5L, null, null, null));

        superService.deleteParticipant(ev2, new UserDTO(4L, null, null, null));

        List<EventDTO> events = superService.getAllEvents(0);
        assertEquals(2, events.size());

        assertEquals(1L, events.get(0).getId());
        assertEquals("Oktoberfest", events.get(0).getName());

        assertEquals(4L, events.get(0).getOrganizer().getId());
        assertEquals("Andrei", events.get(0).getOrganizer().getFirstName());
        assertEquals("Covaciu", events.get(0).getOrganizer().getLastName());
        assertEquals("ake", events.get(0).getOrganizer().getUserName());

        assertEquals("Beer festival", events.get(0).getDescription());
        assertEquals(LocalDateTime.of(2030, 10, 5, 18, 0), events.get(0).getDate());

        assertEquals(4, events.get(0).getParticipants().size());
        assertTrue(events.get(0).getParticipants().contains(new UserDTO(1L, null, null, null)));
        assertTrue(events.get(0).getParticipants().contains(new UserDTO(2L, null, null, null)));
        assertTrue(events.get(0).getParticipants().contains(new UserDTO(3L, null, null, null)));


        assertEquals(2L, events.get(1).getId());
        assertEquals("Pastele Cailor", events.get(1).getName());

        assertEquals(2L, events.get(1).getOrganizer().getId());
        assertEquals("Victor", events.get(1).getOrganizer().getFirstName());
        assertEquals("Doroftei", events.get(1).getOrganizer().getLastName());
        assertEquals("vic", events.get(1).getOrganizer().getUserName());

        assertEquals("Fictiune", events.get(1).getDescription());
        assertEquals(LocalDateTime.of(2277, 12, 30, 22, 35), events.get(1).getDate());

        assertEquals(3, events.get(1).getParticipants().size());
        assertTrue(events.get(1).getParticipants().contains(new UserDTO(1L, null, null, null)));
        assertTrue(events.get(1).getParticipants().contains(new UserDTO(5L, null, null, null)));

        events = superService.getAllEventsForUser(new UserDTO(1L, null, null, null));
        assertEquals(2, events.size());

        assertEquals(1L, events.get(0).getId());
        assertEquals("Oktoberfest", events.get(0).getName());

        assertEquals(4L, events.get(0).getOrganizer().getId());
        assertEquals("Andrei", events.get(0).getOrganizer().getFirstName());
        assertEquals("Covaciu", events.get(0).getOrganizer().getLastName());
        assertEquals("ake", events.get(0).getOrganizer().getUserName());

        assertEquals("Beer festival", events.get(0).getDescription());
        assertEquals(LocalDateTime.of(2030, 10, 5, 18, 0), events.get(0).getDate());

        assertEquals(4, events.get(0).getParticipants().size());
        assertTrue(events.get(0).getParticipants().contains(new UserDTO(1L, null, null, null)));
        assertTrue(events.get(0).getParticipants().contains(new UserDTO(2L, null, null, null)));
        assertTrue(events.get(0).getParticipants().contains(new UserDTO(3L, null, null, null)));

        assertEquals(2L, events.get(1).getId());
        assertEquals("Pastele Cailor", events.get(1).getName());

        assertEquals(2L, events.get(1).getOrganizer().getId());
        assertEquals("Victor", events.get(1).getOrganizer().getFirstName());
        assertEquals("Doroftei", events.get(1).getOrganizer().getLastName());
        assertEquals("vic", events.get(1).getOrganizer().getUserName());

        assertEquals("Fictiune", events.get(1).getDescription());
        assertEquals(LocalDateTime.of(2277, 12, 30, 22, 35), events.get(1).getDate());

        assertEquals(3, events.get(1).getParticipants().size());
        assertTrue(events.get(1).getParticipants().contains(new UserDTO(1L, null, null, null)));
        assertTrue(events.get(1).getParticipants().contains(new UserDTO(5L, null, null, null)));

        assertThrows(ServiceException.class, () -> superService.addParticipant(ev1, new UserDTO(1L, null, null, null)));

        EventDTO ev3 = new EventDTO(1L, "Novemberfest", new UserDTO(5L, null, null, null), "Wine festival", LocalDateTime.of(2025, 7, 8, 19, 55), null);
        superService.updateEvent(ev3);

        events = superService.getAllEvents(0);
        assertEquals("Novemberfest", events.get(0).getName());

        assertEquals("Wine festival", events.get(0).getDescription());
        assertEquals(LocalDateTime.of(2025, 7, 8, 19, 55), events.get(0).getDate());

        List<EventDTO> orgEvents = superService.getAllEventsForOrganizer(new UserDTO(5L, null, null, null));
        assertEquals(0, orgEvents.size());

        superService.removeUser(new UserDTO(5L, "Alex", "Gheorghe", "mattix"));
        List<Event> evs = eventService.getAllEvents(0, 10);
        assertEquals(2, evs.size());

        superService.deleteEvent(ev2);

        events = superService.getAllEventsForUser(new UserDTO(4L, null, null, null));
        assertEquals(1, events.size());

        events = superService.getAllEvents(0);
        assertEquals(1, events.size());

        assertEquals(0, superService.getUpcomingEvents(new UserDTO(1L, null, null, null)).size());

        superService.setNotificationsForUser(ev1, new UserDTO(1L, null, null, null), false);

        assertEquals(0, superService.getUpcomingEvents(new UserDTO(1L, null, null, null)).size());

        superService.removeUser(new UserDTO(1L, null, null, "stefan4556"));

        u1 = new UserDTO(1L, "A", "B", "ab");

        userService.addUser(new User("A", "B", "ab", "P1"));

        ProfileDTO profile1 = new ProfileDTO(u1, "aaa", "bbb", LocalDate.of(2000, 1, 1), "ccc");
        superService.addProfile(profile1, u1);

        Iterable<Profile> profiles = profileRepository.findAll();
        int counter = 0;
        for (Profile ignored : profiles)
            counter++;
        assertEquals(1, counter);

        profile1 = superService.getProfileForUser(u1);

        assertEquals(1L, profile1.getUser().getId());
        assertEquals("aaa", profile1.getAboutMe());
        assertEquals("bbb", profile1.getHomeTown());
        assertEquals(LocalDate.of(2000, 1, 1), profile1.getBirthday());
        assertEquals("ccc", profile1.getHobbies());

        superService.updateProfile(new ProfileDTO(u1, "xxx", "yyy", LocalDate.of(2002, 2, 2), "zzz"));

        profile1 = superService.getProfileForUser(u1);

        assertEquals(1L, profile1.getUser().getId());
        assertEquals("xxx", profile1.getAboutMe());
        assertEquals("yyy", profile1.getHomeTown());
        assertEquals(LocalDate.of(2002, 2, 2), profile1.getBirthday());
        assertEquals("zzz", profile1.getHobbies());

        profiles = profileRepository.findAll();
        counter = 0;
        for (Profile ignored : profiles)
            counter++;
        assertEquals(1, counter);

        superService.deleteProfile(new ProfileDTO(u1, "xxx", "yyy", LocalDate.of(2002, 2, 2), "zzz"));

        profiles = profileRepository.findAll();
        counter = 0;
        for (Profile ignored : profiles)
            counter++;
        assertEquals(0, counter);

        superService.removeUser(u1);

        superService.addUser(new UserRegisterDTO(new UserDTO(null, "X", "Y", "xy"), "P2"));
        profiles = profileRepository.findAll();
        counter = 0;
        for (Profile ignored : profiles)
            counter++;
        assertEquals(1, counter);

        profile1 = superService.getProfileForUser(new UserDTO(1L, "X", "Y", "xy"));

        assertEquals(1L, profile1.getUser().getId());
        assertEquals("", profile1.getAboutMe());
        assertEquals("", profile1.getHomeTown());
        assertEquals("", profile1.getHobbies());

        PostDTO p1 = new PostDTO(1L, new UserDTO(1L, "X", "Y", "xy"), "abc", LocalDateTime.of(2021, 1, 1, 12, 0));
        PostDTO p2 = new PostDTO(2L, new UserDTO(1L, "X", "Y", "xy"), "def", LocalDateTime.of(2022, 1, 1, 12, 0));

        superService.addPost(p1);
        superService.addPost(p2);

        Iterable<Post> it = postRepository.findAll();
        counter = 0;
        for (Post ignored : it)
            counter++;
        assertEquals(2, counter);

        List<PostDTO> posts = superService.getAllPostsForUser(new UserDTO(1L, "X", "Y", "xy"));
        assertEquals(2, posts.size());

        assertEquals(2L, posts.get(0).getId());
        assertEquals(1L, posts.get(0).getUser().getId());
        assertEquals("def", posts.get(0).getContent());
        assertEquals(LocalDateTime.of(2022, 1, 1, 12, 0), posts.get(0).getPostedOn());

        assertEquals(1L, posts.get(1).getId());
        assertEquals(1L, posts.get(1).getUser().getId());
        assertEquals("abc", posts.get(1).getContent());
        assertEquals(LocalDateTime.of(2021, 1, 1, 12, 0), posts.get(1).getPostedOn());

        PostDTO p3 = new PostDTO(2L, new UserDTO(1L, "X", "Y", "xy"), "xyz", LocalDateTime.of(2023, 1, 1, 12, 0));
        superService.updatePost(p3);

        posts = superService.getAllPostsForUser(new UserDTO(1L, "X", "Y", "xy"));
        assertEquals(2, posts.size());

        assertEquals(2L, posts.get(0).getId());
        assertEquals(1L, posts.get(0).getUser().getId());
        assertEquals("xyz", posts.get(0).getContent());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), posts.get(0).getPostedOn());

        superService.deletePost(p3);
        posts = superService.getAllPostsForUser(new UserDTO(1L, "X", "Y", "xy"));
        assertEquals(1, posts.size());

        assertEquals(1L, posts.get(0).getId());
        assertEquals(1L, posts.get(0).getUser().getId());
        assertEquals("abc", posts.get(0).getContent());
        assertEquals(LocalDateTime.of(2021, 1, 1, 12, 0), posts.get(0).getPostedOn());

        superService.deletePost(p1);
        posts = superService.getAllPostsForUser(new UserDTO(1L, "X", "Y", "xy"));
        assertEquals(0, posts.size());

        posts = superService.getAllPostsForUser(new UserDTO(2L, null, null, null));
        assertEquals(0, posts.size());

        eventRepository.save(new Event(10L, "abc", 1L, "def", LocalDateTime.of(2020, 1, 1, 12, 0), null));

        superService.deleteOldEvents();

        Iterable<Event> itEv = eventRepository.findAll();
        int ct = 0;
        for (Event e : itEv)
            ct++;

        assertEquals(1, ct);

    }

    @Test
    public void serviceEventDBTest() {

        PagingRepository<Long, Event> eventRepository = new EventDB();
        EventParticipantsDB eventParticipantsRepository = new EventParticipantsDB();
        EventValidator eventValidator = new EventValidator();
        EventService eventService = new EventService(eventRepository, eventParticipantsRepository, eventValidator);

        Repository<Long, User> userRepository = new UserDB();

        User usr1 = new User("Stefan", "Farcasanu", "stefan4556", null);
        usr1.setId(1L);

        userRepository.save(usr1);

        User usr2 = new User("Victor", "Doroftei", "vic", null);
        usr2.setId(2L);
        userRepository.save(usr2);

        User usr3 = new User("Mihai", "Dretcanu", "mita", null);
        usr3.setId(3L);
        userRepository.save(usr3);

        UserDTO u1 = new UserDTO(1L, "Stefan", "Farcasanu", "stefan4556");
        UserDTO u2 = new UserDTO(2L, "Victor", "Doroftei", "vic");
        UserDTO u3 = new UserDTO(3L, "Mihai", "Dretcanu", "mita");

        EventDTO ev1 = new EventDTO(null, "Oktoberfest", u1, "Beer festival", LocalDateTime.of(2030, 10, 5, 18, 0), null);
        eventService.addEvent(ev1);

        ev1.setId(1L);
        eventService.addParticipant(ev1, new UserDTO(u2.getId(), null, null, null));
        eventService.addParticipant(ev1, new UserDTO(u3.getId(), null, null, null));

        Iterable<Event> events = eventRepository.findAll();

        int ct = 0;
        for (Event e : events)
            ct++;

        assertEquals(1, ct);

        Event ev = eventRepository.findOne(1L);

        assertEquals(1L, ev.getId());
        assertEquals("Oktoberfest", ev.getName());
        assertEquals("Beer festival", ev.getDescription());
        assertEquals(LocalDateTime.of(2030, 10, 5, 18, 0), ev.getDate());

        assertEquals(3, ev.getParticipants().size());
        assertTrue(ev.getParticipants().contains(2L));
        assertTrue(ev.getParticipants().contains(3L));


        EventDTO ev2 = new EventDTO(null, "Pastele Cailor", u1, "Fictiune", LocalDateTime.of(2277, 12, 30, 22, 35), null);
        eventService.addEvent(ev2);

        ev2.setId(2L);
        eventService.addParticipant(ev2, new UserDTO(u3.getId(), null, null, null));

        List<EventDTO> orgEvent = eventService.getAllEventsForOrganizer(u1);
        assertEquals(2, orgEvent.size());

        events = eventRepository.findAll();
        ct = 0;
        for (Event e : events)
            ct++;

        assertEquals(2, ct);

        List<EventDTO> eventList = eventService.getAllEventsForUser(new UserDTO(u1.getId(), null, null, null));
        assertEquals(2, eventList.size());

        eventList = eventService.getAllEventsForUser(new UserDTO(u3.getId(), null, null, null));
        assertEquals(2, eventList.size());
        assertEquals(1L, eventList.get(0).getId());
        assertEquals(2L, eventList.get(1).getId());

        eventService.deleteEvent(new EventDTO(2L, null, null, null, null, null));

        events = eventRepository.findAll();
        ct = 0;
        for (Event e : events)
            ct++;

        assertEquals(1, ct);

        EventDTO ev3 = new EventDTO(1L, "Novemberfest", u2, "Wine festival", LocalDateTime.of(2022, 11, 25, 16, 30), null);
        eventService.updateEvent(ev3);

        ev = eventRepository.findOne(1L);

        assertEquals(1L, ev.getId());
        assertEquals("Novemberfest", ev.getName());
        assertEquals("Wine festival", ev.getDescription());
        assertEquals(LocalDateTime.of(2022, 11, 25, 16, 30), ev.getDate());

        assertThrows(ValidationException.class, () -> eventService.addEvent(new EventDTO(null, null, u1, "aaa", LocalDateTime.of(2300, 1, 1, 17, 1), null)));
        assertThrows(ValidationException.class, () -> eventService.addEvent(new EventDTO(null, "", u1, "aaa", LocalDateTime.of(2300, 1, 1, 17, 1), null)));
        assertThrows(ValidationException.class, () -> eventService.addEvent(new EventDTO(null, "name", u1, null, LocalDateTime.of(2300, 1, 1, 17, 1), null)));
        assertThrows(ValidationException.class, () -> eventService.addEvent(new EventDTO(null, "name", u1, "", LocalDateTime.of(2300, 1, 1, 17, 1), null)));
        assertThrows(ValidationException.class, () -> eventService.addEvent(new EventDTO(null, "name", u1, "aaa", null, null)));
        assertThrows(ValidationException.class, () -> eventService.addEvent(new EventDTO(null, "name", u1, "", LocalDateTime.of(2000, 1, 1, 17, 1), null)));

        ev = eventRepository.findOne(1L);
        assertEquals(3, ev.getParticipants().size());
        assertTrue(ev.getParticipants().contains(1L));
        assertTrue(ev.getParticipants().contains(2L));
        assertTrue(ev.getParticipants().contains(3L));

        assertThrows(ServiceException.class, () -> eventService.addParticipant(new EventDTO(1L, null, null, null, null, null), new UserDTO(u1.getId(), null, null, null)));
        assertThrows(ServiceException.class, () -> eventService.addParticipant(new EventDTO(1L, null, null, null, null, null), new UserDTO(u3.getId(), null, null, null)));

        eventService.deleteParticipant(ev3, new UserDTO(u3.getId(), null, null, null));

        ev = eventRepository.findOne(1L);
        assertEquals(2, ev.getParticipants().size());
        assertTrue(ev.getParticipants().contains(1L));
        assertTrue(ev.getParticipants().contains(2L));

        usr1 = new User("Popa", "Andrei", "PopaAndrei", null);
        usr1.setId(10L);

        userRepository.save(usr1);

        usr2 = new User("Popa1", "Andrei1", "PopaAndrei1", null);
        usr2.setId(11L);
        userRepository.save(usr2);

        usr3 = new User("Popa2", "Andrei2", "PopaAndrei2", null);
        usr3.setId(17L);
        userRepository.save(usr3);

        EventDTO ev4 = new EventDTO(null, "Pastele Cailor V2", u1, "Fictiune V22", LocalDateTime.of(2277, 12, 30, 22, 35), null);
        eventService.addEvent(ev4);

        assertThrows(ServiceException.class, () -> eventService.deleteParticipant(ev4, u1));

        UserDTO u4 = new UserDTO(10L, "Popa", "Andrei", "PopaAndrei");
        EventDTO e4 = new EventDTO(3L, "Untoldddd", u4, "Muzica buna si relaxare", LocalDateTime.of(2277, 12, 30, 22, 35), null);

        UserDTO u5 = new UserDTO(11L, "Popa1", "Andrei1", "PopaAndrei1");

        eventService.addEvent(e4);
        eventService.addParticipant(e4, u5);

        assertEquals(0, eventService.getUpcomingEvents(u5).size());

        eventService.setNotificationsForUser(e4, u5, false);

        assertEquals(0, eventService.getUpcomingEvents(u5).size());

        UserDTO u6 = new UserDTO(17L, "Popa2", "Andrei2", "PopaAndrei2");

        assertThrows(ServiceException.class, () -> eventService.setNotificationsForUser(e4, u6, false));

        Event e5 = new Event(4L, "Festival1", 10L, "Desc1", LocalDateTime.of(2021, 1, 1, 12, 0), null);
        Event e6 = new Event(5L, "Festival1", 10L, "Desc1", LocalDateTime.of(2020, 1, 1, 12, 0), null);
        Event e7 = new Event(6L, "Festival1", 10L, "Desc1", LocalDateTime.of(2019, 1, 1, 12, 0), null);

        eventRepository.save(e5);
        eventRepository.save(e6);
        eventRepository.save(e7);

        eventService.deleteOldEvents();

        Iterable<Event> eventIterable = eventRepository.findAll();
        ct = 0;
        for (Event ignored : eventIterable)
            ct++;

        assertEquals(3, ct);

    }

    @Test
    public void pagingRepoTest() {

        PagingRepository<Long, Message> messagePagingRepository = new MessageDB();

        Repository<Long, User> userRepository = new UserDB();

        User u1 = new User("a", "b", "ab", null);
        u1.setId(1L);

        userRepository.save(u1);

        User u2 = new User("a", "b", "gh", null);
        u2.setId(2L);
        userRepository.save(u2);

        for (int i = 7; i < 27; i++) {

            messagePagingRepository.save(new Message((long) i, 1L, List.of(2L), "Servus dragule" + String.valueOf(i)));
        }

        List<Message> messages = new ArrayList<>();

        Iterable<Message> iterable = messagePagingRepository.findAll();

        iterable.forEach(messages::add);

        System.out.println(messages.stream().map(Entity::getId).collect(Collectors.toList()));

        System.out.println(messages.stream().skip(1L).map(Entity::getId).collect(Collectors.toList()));

        messagePagingRepository.findAll(new PageableImplementation(0, 1), iterable).getContent().forEach(x -> System.out.println(x.getId()));

        messagePagingRepository.findAll(new PageableImplementation(0, 5), iterable).getContent().forEach(x -> System.out.println(x.getId()));

        messagePagingRepository.findAll(new PageableImplementation(10, 1), iterable).getContent().forEach(x -> System.out.println(x.getId()));
    }

}