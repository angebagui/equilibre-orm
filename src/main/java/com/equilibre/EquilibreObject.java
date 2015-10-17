package com.equilibre;

import com.j256.ormlite.field.DatabaseField;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import bolts.Task;

/**
 * Created by angebagui on 24/08/2015.
 */
public class EquilibreObject  {

    /**
     * Default Id
     */
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private Date createdAt;
    @DatabaseField
    private Date updatedAt;

    private static final Map<Class<? extends EquilibreObject>, String> classNames = new ConcurrentHashMap<Class<? extends EquilibreObject>, String>();
    private static final Map<String, Class<? extends EquilibreObject>> objectTypes = new ConcurrentHashMap<String, Class<? extends EquilibreObject>>();


    private static Object lock = new Object();
    private String className;
    public Integer getId() {
        return id;
    }

    /**
     *
     * @return the Date of object creation
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt the Date of object creation
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return the Date of object updating
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt the Date of object updating
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public EquilibreObject(){

    }

    public EquilibreObject(String className){
        this.className = className;
    }

    /**
     * Registers a custom subclass type with the Equilibre Library, enabling strong-typing of those
     * {@code EquilibreObject}s whenever they appear. Subclasses must specify the {@link EquilibreClassName}
     * annotation and have a default constructor.
     *
     * @param subclass
     *          The subclass type to register.
     */
    public static void registerSubclass(Class<? extends EquilibreObject> subclass) {
        String className =  getClassName(subclass);
        if (className==null){
            throw new IllegalArgumentException("No EquilibreClassName annotation provided on " + subclass);
        }

        if (subclass.getDeclaredConstructors().length>0){
            try {
                if (!isAccessible(subclass.getDeclaredConstructor())) {
                    throw new IllegalArgumentException("Default constructor for " + subclass
                            + " is not accessible.");
                }
            }catch (NoSuchMethodException e){
                throw new IllegalArgumentException("No default constructor provided for " + subclass);
            }
        }
        Class<? extends EquilibreObject> oldValue = objectTypes.get(className);
        if (oldValue != null && subclass.isAssignableFrom(oldValue)) {
            // The old class was already more descendant than the new subclass type. No-op.
            return;
        }
        objectTypes.put(className, subclass);

    }


    private static boolean isAccessible(Member m) {
        return Modifier.isPublic(m.getModifiers())
                || (m.getDeclaringClass().getPackage().getName().equals("com.equilibre")
                && !Modifier.isPrivate(m.getModifiers()) && !Modifier.isProtected(m.getModifiers()));
    }

    /* package for tests */ static void unregisterSubclass(Class<? extends EquilibreObject> subclass) {
        unregisterSubclass(getClassName(subclass));
    }

    /* package for tests */ static void unregisterSubclass(String className) {
        objectTypes.remove(className);
    }

     static String getClassName(Class<? extends EquilibreObject> subclass){
        String name = classNames.get(subclass);
        if (name==null){
            EquilibreClassName info = subclass.getAnnotation(EquilibreClassName.class);
            if (info==null){
                return null;
            }
            name = info.value();
            classNames.put(subclass, name);
        }
        return name;
    }

    public static Collection<Class<? extends EquilibreObject>> getClasses(){
        synchronized (lock){
            return objectTypes.values();
        }
    }

    /**
     * Stores the object and every object it points to in the local datastore, recursively.
     *
     * @see #deleteAsync(DeleteCallback)
     *
     * @param callback
     *          the callback for asynchronous execution
     * @param <T> the EquilibreObject's subclass
     */
    public <T extends EquilibreObject> void saveAsync(SaveCallback callback){
        EquilibreQuery query = EquilibreQuery.getQuery(this.getClass());
        query.saveInBackground(this, callback);
    }

    /**
     * Store the object in the local datastore, without response.
     *
     * @param <T>  the EquilibreObject's subclass
     * @throws EquilibreException an exception
     */
    public <T extends EquilibreObject> void save() throws EquilibreException {
        EquilibreQuery query = EquilibreQuery.getQuery(this.getClass());

        query.save(this);

    }

    /**
     * Delete the object in the local datastore, with callback as a container of the response
     *
     * @param callback the callback for asynchronous execution
     * @param <T> the EquilibreObject's subclass
     */
    public <T extends EquilibreObject> void deleteAsync(DeleteCallback callback){
        EquilibreQuery query = EquilibreQuery.getQuery(this.getClass());
        query.deleteInBackground(this, callback);
    }
    /**
     * Delete the object in the local datastore, without response.
     *
     * @param <T> the EquilibreObject's subclass
     *     @throws EquilibreException an exception
     */
    public <T extends EquilibreObject> void delete() throws EquilibreException {
        EquilibreQuery query = EquilibreQuery.getQuery(this.getClass());
            query.delete(this);

    }

    /**
     *  Update the object in the local datastore, with {@link RefreshCallback} as a container of the answer
     *
     * @param callback the callback for asynchronous execution
     * @param <T>  the EquilibreObject's subclass
     */
    public <T extends EquilibreObject> void refreshAsync(RefreshCallback callback){
        EquilibreQuery query = EquilibreQuery.getQuery(this.getClass());
        query.refreshInBackground(this, callback);
    }

    /**
     * Update the object in the local datastore, without answer
     * @param <T>  the EquilibreObject's subclass
     * @throws EquilibreException
     *           Throws an exception if the server returns an error or is inaccessible.
     */
    public <T extends EquilibreObject> void refresh() throws EquilibreException {
        EquilibreQuery query = EquilibreQuery.getQuery(this.getClass());
        query.refresh(this);
    }

    /**
     * Saves each object in the provided list. This is faster than saving each object individually
     * because it batches the requests.
     *
     * @param objects
     *          The objects to save.
     * @throws EquilibreException
     *           Throws an exception if the server returns an error or is inaccessible.
     * @param <T> the EquilibreObject's subclass
     */
    public static <T extends EquilibreObject> void saveAll(List<T> objects) throws EquilibreException {
        EquilibreTaskUtils.wait(saveAllInBackground(objects));
    }
    /**
     * Saves each object in the provided list to the server in a background thread. This is preferable
     * to using saveAll, unless your code is already running from a background thread.
     *
     * @param objects
     *          The objects to save.
     * @param callback
     *          {@code callback.done(e)} is called when the save completes.
     *
     * @param <T> the EquilibreObject's subclass
     */
    public static <T extends EquilibreObject> void saveAllInBackground(List<T> objects, SaveCallback callback){
        EquilibreTaskUtils.callbackOnMainThreadAsync(saveAllInBackground(objects), callback);
    }

    /**
     * Saves each object in the provided list to the server in a background thread. This is preferable
     * to using saveAll, unless your code is already running from a background thread.
     *
     * @param objects
     *          The objects to save.
     * @param <T> the EquilibreObject's subclass
     *
     * @return A task that is resolved when saveAll completes.
     */
    public static <T extends EquilibreObject> Task<Void> saveAllInBackground(final List<T> objects) {

        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                EquilibreQuery query = EquilibreQuery.getQuery(objects.get(0).getClass());
                for (EquilibreObject object: objects){
                    query.getDao().create(object);

                }
                return null;
            }
        });
    }


}
