package com.example.laborator5.socialnetwork.service;

import com.example.laborator5.socialnetwork.domain.Post;
import com.example.laborator5.socialnetwork.repository.Repository;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.example.laborator5.socialnetwork.utils.observer.Observable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PostService implements Observable {

    /**
     * Post repository.
     */
    private Repository<Long, Post> repo;

    /**
     * Observer list.
     */
    private List<Observer> observers = new ArrayList<>();

    /**
     * Constructor method for the PostService class.
     *
     * @param repo the post repository
     */
    public PostService(Repository<Long, Post> repo) {

        this.repo = repo;
    }

    /**
     * Method for inserting a post into the repository.
     *
     * @param post the post that will be inserted
     */
    public void addPost(Post post) {

        post.setId(this.generateIdForPost());

        this.repo.save(post);

        this.updateAll();

    }

    /**
     * Method for deleting a post from the repository.
     *
     * @param post the post that will be deleted
     */
    public void deletePost(Post post) {

        this.repo.delete(post);

        this.updateAll();

    }

    /**
     * Method for updating a post in the repository.
     *
     * @param post the post that will be updated
     */
    public void updatePost(Post post) {

        this.repo.update(post);

        this.updateAll();

    }

    /**
     * Method for returning all the posts of a given user.
     *
     * @param user the user for which the posts are returned
     * @return a list of posts for the given user, sorted chronologically
     */
    public List<Post> getAllPostsForUser(UserDTO user) {

        Iterable<Post> it = this.repo.findAll();

        List<Post> result = new ArrayList<>();
        for (Post p : it) {

            if (p.getIdUser().equals(user.getId()))
                result.add(p);
        }

        result.sort(Comparator.comparing(Post::getPostedOn, Comparator.reverseOrder()));

        return result;

    }

    /**
     * Method for generating the next available id for a new post.
     *
     * @return the generated id (long)
     */
    private Long generateIdForPost() {

        Long id = 1L;
        while (this.repo.findOne(id) != null)

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
}
