package com.jhj.refreshrecyclerview.bean;

import java.util.List;

/**
 * Created by jhj on 18-10-22.
 */

public class ApplyBean {


    /**
     * reason : hh
     * endDate : 2017-03-23
     * editable : false
     * nextActorMemberId : 958
     * workFlowList : [{"actTime":1490229682255,"actorName":"王宏斌","operation":"提交","memberId":"754"}]
     * creatorName : 王宏斌
     * nextActorId : 177
     * revocable : true
     * leaveType : 1
     * attachmentList : [{"fileName":"splashconfig","fileSize":"3891","fileUrl":"http://www.97gyl.com/upload/file/askleave202/20170323/754/splashconfig"}]
     * createTime : 1490229682244
     * examinePermissions : 0
     * nextActorName : 周磊
     * leaveHourNum : 0
     * leaveDayNum : 1
     * leaveTypeName : 事假
     * id : 80
     * state : 0
     * startDate : 2017-03-23
     */

    private String reason;
    private String endDate;
    private boolean editable;
    private int nextActorMemberId;
    private String creatorName;
    private int nextActorId;
    private boolean revocable;
    private int leaveType;
    private long createTime;
    private int examinePermissions;
    private String nextActorName;
    private int leaveHourNum;
    private int leaveDayNum;
    private String leaveTypeName;
    private String id;
    private int state;
    private String startDate;
    private List<WorkFlowListBean> workFlowList;
    private List<AttachmentListBean> attachmentList;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public int getNextActorMemberId() {
        return nextActorMemberId;
    }

    public void setNextActorMemberId(int nextActorMemberId) {
        this.nextActorMemberId = nextActorMemberId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public int getNextActorId() {
        return nextActorId;
    }

    public void setNextActorId(int nextActorId) {
        this.nextActorId = nextActorId;
    }

    public boolean isRevocable() {
        return revocable;
    }

    public void setRevocable(boolean revocable) {
        this.revocable = revocable;
    }

    public int getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(int leaveType) {
        this.leaveType = leaveType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getExaminePermissions() {
        return examinePermissions;
    }

    public void setExaminePermissions(int examinePermissions) {
        this.examinePermissions = examinePermissions;
    }

    public String getNextActorName() {
        return nextActorName;
    }

    public void setNextActorName(String nextActorName) {
        this.nextActorName = nextActorName;
    }

    public int getLeaveHourNum() {
        return leaveHourNum;
    }

    public void setLeaveHourNum(int leaveHourNum) {
        this.leaveHourNum = leaveHourNum;
    }

    public int getLeaveDayNum() {
        return leaveDayNum;
    }

    public void setLeaveDayNum(int leaveDayNum) {
        this.leaveDayNum = leaveDayNum;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public List<WorkFlowListBean> getWorkFlowList() {
        return workFlowList;
    }

    public void setWorkFlowList(List<WorkFlowListBean> workFlowList) {
        this.workFlowList = workFlowList;
    }

    public List<AttachmentListBean> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<AttachmentListBean> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public static class WorkFlowListBean {
        /**
         * actTime : 1490229682255
         * actorName : 王宏斌
         * operation : 提交
         * memberId : 754
         */

        private long actTime;
        private String actorName;
        private String operation;
        private String memberId;

        public long getActTime() {
            return actTime;
        }

        public void setActTime(long actTime) {
            this.actTime = actTime;
        }

        public String getActorName() {
            return actorName;
        }

        public void setActorName(String actorName) {
            this.actorName = actorName;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }
    }

    public static class AttachmentListBean {
        /**
         * fileName : splashconfig
         * fileSize : 3891
         * fileUrl : http://www.97gyl.com/upload/file/askleave202/20170323/754/splashconfig
         */

        private String fileName;
        private String fileSize;
        private String fileUrl;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }
}
