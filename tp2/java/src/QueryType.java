/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */


/**
 * The {@code QueryType} enum represents different types of queries that the QueryHandler class can process.
 * It categorizes queries into specific types, such as BIGRAM or SEARCH, enabling the QueryHandler to apply
 * the appropriate processing logic for each type.
 * <p>
 * Each enum constant includes a query prefix that helps differentiate and process the respective queries.
 */
public enum QueryType {
    /**
     * Represents a query type for finding the most probable bigram of a given word.
     * This type of query is used to identify and analyze bigrams within a dataset.
     */
    BIGRAM("the most probable bigram of "),

    /**
     * Represents a query type for searching and identifying the most relevant file
     * based on the Term Frequency-Inverse Document Frequency (TFIDF) scores of a given word.
     * This type of query is used for information retrieval and document relevance assessment.
     */
    SEARCH("search "),
    ;
    /**
     * The prefix associated with each query type.
     */
    private final String queryPrefix;

    /**
     * Constructs a QueryType enum constant with the specified query prefix.
     *
     * @param queryPrefix the prefix associated with the query type
     */
    QueryType(String queryPrefix) {
        this.queryPrefix = queryPrefix;
    }

    /**
     * Gets the query prefix associated with the QueryType.
     *
     * @return the query prefix
     */
    public String getQueryPrefix() {
        return queryPrefix;
    }
}
