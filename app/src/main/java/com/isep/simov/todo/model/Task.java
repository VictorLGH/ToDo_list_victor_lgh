package com.isep.simov.todo.model;

import com.google.android.gms.location.places.Place;

import java.util.Date;
import java.util.HashMap;

public class Task {

    private String taskId;
    private String userId;
    private String localizationId;
    private String taskName;
    private String taskDescription;
    private Date createdDate;
    private Date endDate;
    private boolean isDone;
    private boolean isPriority;
    private String tags;
    private long dateTimeStamp;

    public HashMap<String, Object> getLocalization() {
        return localization;
    }

    public void setLocalization(HashMap<String, Object> localization) {
        this.localization = localization;
    }

    private HashMap<String, Object> localization;

    public Task() {
    }

    public Task(String taskName, String taskDescription, boolean priority, String tags, Date createdDate, Date endDate, String userId, Place place, long dateTimeStamp) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.createdDate = createdDate;
        this.endDate = endDate;
        this.isDone = false;
        this.isPriority = priority;
        this.tags = tags;
        this.userId = userId;
        this.dateTimeStamp = dateTimeStamp;
        if (place != null) {
            this.localization = new HashMap<>();
            localization.put("placeGoogleId", place.getId());
            localization.put("placeName", place.getName().toString());
            localization.put("placeAddress", place.getAddress().toString());
            localization.put("lat", place.getLatLng().latitude);
            localization.put("lon", place.getLatLng().longitude);
        }

    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public void setPriority(boolean priority) {
        isPriority = priority;
    }


    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getLocalizationId() {
        return localizationId;
    }

    public void setLocalizationId(String localizationId) {
        this.localizationId = localizationId;
    }

    public long getDateTimeStamp() {
        return dateTimeStamp;
    }

    public void setDateTimeStamp(long dateTimeStamp) {
        this.dateTimeStamp= dateTimeStamp;
    }
}
