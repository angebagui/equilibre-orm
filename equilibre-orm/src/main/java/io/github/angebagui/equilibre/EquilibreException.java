package io.github.angebagui.equilibre;

/**
 * Created by angebagui on 19/08/2015.
 */
public class EquilibreException extends Exception {

    private static final int OTHER_CAUSE = -1;
    private int code;
    /**
     * Error code indicating that something has gone wrong with the server.
     *
     */
    public static final int INTERNAL_SERVER_ERROR = 1;

    /**
     * Error code indicating the connection to Your servers failed.
     */
    public static final int CONNECTION_FAILED = 100;

    /**
     * Error code indicating the specified object doesn't exist.
     */
    public static final int OBJECT_NOT_FOUND = 101;

    /**
     * Error code indicating you tried to query with a datatype that doesn't support it, like exact
     * matching an array or object.
     */
    public static final int INVALID_QUERY = 102;

    /**
     * Error code indicating a missing or invalid classname. Classnames are case-sensitive. They must
     * start with a letter, and a-zA-Z0-9_ are the only valid characters.
     */
    public static final int INVALID_CLASS_NAME = 103;

    /**
     * Error code indicating an unspecified object id.
     */
    public static final int MISSING_OBJECT_ID = 104;

    /**
     * Error code indicating an invalid key name. Keys are case-sensitive. They must start with a
     * letter, and a-zA-Z0-9_ are the only valid characters.
     */
    public static final int INVALID_KEY_NAME = 105;

    /**
     * Error code indicating a malformed pointer. You should not see this unless you have been mucking
     * about changing internal Parse code.
     */
    public static final int INVALID_POINTER = 106;

    /**
     * Error code indicating that badly formed JSON was received upstream. This either indicates you
     * have done something unusual with modifying how things encode to JSON, or the network is failing
     * badly.
     */
    public static final int INVALID_JSON = 107;

    /**
     * Error code indicating that the feature you tried to access is only available internally for
     * testing purposes.
     */
    public static final int COMMAND_UNAVAILABLE = 108;

    /**
     * You must call Equilibre.initialize before using the Parse library.
     */
    public static final int NOT_INITIALIZED = 109;

    /**
     * Error code indicating that a field was set to an inconsistent type.
     */
    public static final int INCORRECT_TYPE = 111;


    /**
     * Error code indicating that push is misconfigured.
     */
    public static final int PUSH_MISCONFIGURED = 115;

    /**
     * Error code indicating that the object is too large.
     */
    public static final int OBJECT_TOO_LARGE = 116;

    /**
     * Error code indicating that the operation isn't allowed for clients.
     */
    public static final int OPERATION_FORBIDDEN = 119;

    /**
     * Error code indicating the result was not found in the cache.
     */
    public static final int CACHE_MISS = 120;

    /**
     * Error code indicating that an invalid key was used in a nested JSONObject.
     */
    public static final int INVALID_NESTED_KEY = 121;

    /**
     * Error code indicating that an invalid filename was used for ParseFile. A valid file name
     * contains only a-zA-Z0-9_. characters and is between 1 and 128 characters.
     */
    public static final int INVALID_FILE_NAME = 122;

    /**
     * Error code indicating that the request timed out on the server. Typically this indicates that
     * the request is too expensive to run.
     */
    public static final int TIMEOUT = 124;


    /**
     * Error code indicating that a unique field was given a value that is already taken.
     */
    public static final int DUPLICATE_VALUE = 137;




    /**
     * Error code indicating that deleting a file failed.
     */
    public static final int FILE_DELETE_ERROR = 153;

    /**
     * Error code indicating that the application has exceeded its request limit.
     */
    public static final int REQUEST_LIMIT_EXCEEDED = 155;

    /**
     * Error code indicating that the provided event name is invalid.
     */
    public static final int INVALID_EVENT_NAME = 160;


    /**
     * Error code indicating that a service being linked (e.g. Facebook or Twitter) is unsupported.
     */
    public static final int UNSUPPORTED_SERVICE = 252;

    /**
     * Construct a new EquilibreException with a particular error code.
     *
     * @param code
     *          The error code to identify the type of exception.
     * @param detailMessage
     *          A message describing the error in more detail.
     */
    public EquilibreException(int code, String detailMessage){
        super(detailMessage);
        this.code=code;
    }

    /**
     * Construct a new EquilibreException with an external cause.
     *
     * @param code
     *          The error code to identify the type of exception.
     * @param detailMessage
     *          A message describing the error in more detail.
     * @param e
     *          The cause of the error.
     */
    public EquilibreException(int code, String detailMessage, Throwable e){
        super(detailMessage,e);
        this.code = code;
    }

    /**
     * Construct a new EquilibreException with an external cause.
     *
     * @param cause
     *        The cause of the error.
     */
    public EquilibreException(Throwable cause){
        super(cause);
        code = OTHER_CAUSE;
    }


    /**
     * Access the code for this error.
     *
     * @return The numerical code for this error.
     */
    public int getCode() {
        return code;
    }

}
