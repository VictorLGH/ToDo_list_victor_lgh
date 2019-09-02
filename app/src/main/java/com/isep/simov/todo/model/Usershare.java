package com.isep.simov.todo.model;

public class Usershare {
    public Usershare(String userInId,String userOutId, String taskID) {
        this.userInId = userInId;
        this.userOutId = userOutId;
        this.taskID = taskID;
    }

    public String getUserInId() {
        return userInId;
    }

    public void setUserInId(String userInId) {
        this.userInId = userInId;
    }

    public String getUserOutId() {
        return userOutId;
    }

    public void setUserOutId(String userOutId) {
        this.userOutId = userOutId;
    }

    public String gettaskID() {
        return taskID;
    }

    public void settaskID(String taskID) {
        this.taskID= taskID;
    }


    private String userInId;
    private String userOutId;
    private String taskID;


}
