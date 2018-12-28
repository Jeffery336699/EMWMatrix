package cc.emw.mobile.task.constant;

public interface TaskConstant {
     String SEND_USERFENPAI = "send_userfenpai";// 发送广播携带数据

    /**
     * 任务状态
     *
     * @author chengyong.liu
     */
     interface TaskState {
        int UNSTART = 1;// 未开始的任务ID
        int PROCESSING = 2;// 进行中的任务ID
        int DELAY = 3;//任务延时
        int FINISHED = 4;// 已经完成的任务ID
    }

    interface TaskStateString {
        String UNSTART = "未开始";// 未开始的任务
        String PROCESSING = "进行中";// 进行中的任务
        String FINISHED = "已完成";// 已经完成;的任务
        String DELAY = "已延迟"; //已延迟的任务
    }

    // 任务详情编辑携带任务数据
    String TASK_MODIFY = "task_modify";
    String TASK_FLAG = "task_flag";// 任务标记
    int CREATE_TASK = 1;// 新建任务
    int EDIT_TASK = 2;// 编辑任务
    int DEVIDE_TASK = 3;// 分解任务

    /**
     * 任务紧急状态
     *
     * @author chengyong.liu
     */
    interface TaskEmergencyState {
        String NORMAL = "普通";
        String EMERGENCY = "紧急";
        String VERY_EMERGENCY = "非常紧急";
    }

    interface TaskEmergency {
        int NORMAL = 1;
        int EMERGENCY = 2;
        int VERY_EMERGENCY = 3;
    }

    /**
     * 任务详情界面分类
     *
     * @author chengyong.liu
     */
    interface TaskDetail {
        String SUMMARIZE = "概览";
        String COMMENT = "评论";
        String ATTACHMENT = "附件";

    }

    /**
     * 任务权限
     *
     * @author chengyong.liu
     */
     interface FlowState {
        int NORMAL = 1; // 正常状态
        int CHECK = 2;// 提交审核
        int RETURNWOEK = 3;// 返工
        int FINISHED = 4;// 已完成
    }

}
