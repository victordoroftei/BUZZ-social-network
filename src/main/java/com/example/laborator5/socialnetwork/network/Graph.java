package com.example.laborator5.socialnetwork.network;

import com.example.laborator5.socialnetwork.domain.Friendship;
import com.example.laborator5.socialnetwork.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * The class which finds and explores all the connections of the social network
 */
public class Graph {

    /**
     * The adjacency list of the graph.
     */
    private HashMap<Long, List<Long>> adjList;

    /**
     * The vertex list of the graph.
     */
    private List<Long> verticesList;

    /**
     * Class for creating an object to memorize the maximum value
     */
    private class MaximumValue {

        /**
         * The maximum value
         */
        private int value;

        /**
         * Constructor of the class
         */
        public MaximumValue() {

            value = 0;
        }

        /**
         * Setter method for the value variable
         *
         * @param newValue the new value for the MaximumValue variable
         */
        void setValue(int newValue) {

            this.value = newValue;
        }

        /**
         * Getter method for the value
         *
         * @return the value
         */
        int getValue() {

            return this.value;
        }
    }

    /**
     * Constructor
     *
     * @param users       - Iterable<User></User> users list
     * @param friendships - Iterable<Friendship></Friendship> friendships list
     */
    public Graph(Iterable<User> users, Iterable<Friendship> friendships) {

        adjList = new HashMap<>();
        verticesList = new ArrayList<>();

        for (User user : users) {

            List<Long> l = new ArrayList<>();
            adjList.put(user.getId(), l);
            verticesList.add(user.getId());
        }
        for (Friendship friendship : friendships) {

            List<Long> l1 = this.adjList.get(friendship.getId().getLeft());
            l1.add(friendship.getId().getRight());
            List<Long> l2 = this.adjList.get(friendship.getId().getRight());
            l2.add(friendship.getId().getLeft());
            this.adjList.put(friendship.getId().getLeft(), l1);
            this.adjList.put(friendship.getId().getRight(), l2);
        }
    }

    /**
     * The method which traverses the network
     *
     * @param v                          - Long, the current node
     * @param currentConnectedComponents - List<Long>, the current connected component
     * @param visited                    - HashMap<Long,Boolean>, the visited nodes
     * @param depth                      - HashMap<Long,Long>, the distance map
     */
    private void DFS(Long v, List<Long> currentConnectedComponents, HashMap<Long, Boolean> visited, HashMap<Long, Long> depth) {

        visited.put(v, true);
        currentConnectedComponents.add(v);
        for (Long x : adjList.get(v)) {
            if (!visited.get(x)) {
                depth.put(x, depth.get(v) + 1);
                DFS(x, currentConnectedComponents, visited, depth);
            }
        }
    }

    /**
     * Method for returning all the connected components
     *
     * @return the list of connected components
     */
    public List<List<Long>> connectedComponents() {

        List<List<Long>> connectedComponentsFound = new ArrayList<>();
        HashMap<Long, Boolean> visited = new HashMap<>();
        HashMap<Long, Long> depth = new HashMap<>();
        for (Long v : verticesList) {
            visited.put(v, false);
            depth.put(v, 0L);
        }

        for (Long id : verticesList)
            if (!visited.get(id)) {
                List<Long> currentConnectedComponents = new ArrayList<>();
                DFS(id, currentConnectedComponents, visited, depth);
                connectedComponentsFound.add(currentConnectedComponents);
            }

        return connectedComponentsFound;
    }

    // Another methods(heuristics): DFS from each node counting the distance from source to the node
    //                              MST, then modified BFS

    /**
     * THe method which finds the longest path
     *
     * @param currentCase - List<Long>, list of current nodes
     * @param currentMax  - MaximumValue, maximum path size
     */
    private void backtracking(List<Long> currentCase, MaximumValue currentMax) {

        if (currentCase.size() > currentMax.getValue())

            currentMax.setValue(currentCase.size());

        for (Long n : this.adjList.get(currentCase.get(currentCase.size() - 1))) {

            if (!currentCase.contains(n)) {
                currentCase.add(n);
                backtracking(currentCase, currentMax);
                currentCase.remove(n);
            }
        }
    }

    /**
     * Finds the longest path in a connected component
     *
     * @param vertices - List<Long>, which contains the list of graph's vertices
     * @return the size of the longest path
     */
    private int longestPathForAConnectedComponent(List<Long> vertices) {

        int max = 0;

        if (vertices.size() == 1) // means the maximum length of the path is 0

            return 0;

        for (Long v : vertices) {

            List<Long> currentCase = new ArrayList<>();
            currentCase.add(v);
            MaximumValue currentMax = new MaximumValue();
            backtracking(currentCase, currentMax);
            if (currentMax.getValue() > max)
                max = currentMax.getValue();
        }
        return max;
    }

    /**
     * Get the most sociable connection
     *
     * @return the list containing the IDs of the users which form the biggest community
     */
    public List<Long> getTheMostSociableConnection() {

        List<Long> result = new ArrayList<>();

        int max = 0;

        List<List<Long>> connectedComponents = this.connectedComponents();

        for (List<Long> component : connectedComponents) {

            int currentMax = longestPathForAConnectedComponent(component);

            if (currentMax > max) {

                max = currentMax;
                result = component;
            }
        }

        return result;
    }
}