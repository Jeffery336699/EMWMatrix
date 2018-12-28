package cc.emw.mobile.project.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePickerView.Type;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.adapter.ProjectAdapter;
import cc.emw.mobile.project.adapter.ResponserAdapter;
import cc.emw.mobile.project.fragment.SummaryFragment;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;

/**
 * 编辑和修改项目页面
 * @author jven.wu
 *
 */
@ContentView(R.layout.activity_modify_project)
public class ModifyProjectActivity extends BaseActivity 
	implements IModifyProjectView {
	private static final String TAG = "ModifyProjectActivity";
	private static final int TASKS = 10; //相关任务标记常量
	private static final int SCHEDULE = 11; //相关日程标记常量
	private static final int REPOSITORY = 12; //知识库标记常量
	public static final int EDIT_PROJECT_CODE = 0;
	public static final String EDIT_PROJECT = "EDIT_PROJECT";
	public static final String ACTION_MODIFY_PROJECT = "cc.emw.mobile.modify_project";
	public static final String ACTION_CREATE_PROJECT = "cc.emw.mobile.create_project";
    public static final int MODIFY = 1;
	
	@ViewInject(R.id.cm_header_btn_left)	//顶部条返回按钮
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)	//顶部条标题
	private TextView mHeaderTitleTv;
	@ViewInject(R.id.cm_header_tv_right)	//顶部完成按钮
	private TextView mHeaderFinishBtn;
	@ViewInject(R.id.et_projectmodify_name)
	private EditText mName;
	@ViewInject(R.id.et_projectmodify_description)
	private EditText mDescription;
	@ViewInject(R.id.tv_projectmodify_state_tip)
	private TextView mStateTip;
	@ViewInject(R.id.tv_projectmodify_state)
	private TextView mState;
	@ViewInject(R.id.tv_projectmodify_progress)
	private TextView mProgress;
	@ViewInject(R.id.pb_projectmodify_progress)
	private ProgressBar mProgressBar;
	@ViewInject(R.id.tv_projectmodify_startTime)
	private TextView mStartTime;
	@ViewInject(R.id.tv_projectmodify_endTime)
	private TextView mEndTime;
	@ViewInject(R.id.iv_projectmodify_portrait)
	private ImageView mPortrait;
	@ViewInject(R.id.tv_projectmodify_responser)
	private TextView mResponser;
	@ViewInject(R.id.lv_projectdetail_responser)
	private ListView mResponserListView;
	@ViewInject(R.id.ll_projectmodify_add)
	private LinearLayout mAddMemberBtn;
	@ViewInject(R.id.ll_projectmodify_relative_task)
	private LinearLayout mRelativeTaskBtn;
	@ViewInject(R.id.iv_projectmodify_taskNum)
	private TextView mTaskNum;
	@ViewInject(R.id.ll_projectmodify_active_schedule)
	private LinearLayout mActiveSchedule;
	@ViewInject(R.id.iv_projectmodify_scheduleNum)
	private TextView mScheduleNum;
	@ViewInject(R.id.ll_projectmodify_repository)
	private LinearLayout mRepository;
	@ViewInject(R.id.iv_projectmodify_repositoryNum)
	private TextView mRepositoryNum;
	
	private int date_type = 0;
	private int projectState = 0;
	private TimePickerView mStartPopupWindow;
	private ProjectPresenter presenter;
	private UserProject project;
	private ResponserAdapter responserAdapter;
	private ArrayList<UserInfo> users = new ArrayList<UserInfo>();
	private ArrayList<Files> files = new ArrayList<Files>();
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		//添加负责人
        if(requestCode == ContactSelectActivity.MULTI_SELECT && resultCode == RESULT_OK){  
        	users = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
        	Log.d(TAG, "user count: "+users.size());
        	responserAdapter.setArrayUser(users,true);
        	mResponserListView.setAdapter(responserAdapter);
        	responserAdapter.notifyDataSetChanged();
        	CommonUtil.setListViewHeightBasedOnChildren(mResponserListView);
        }
        //关连相关任务
        if(requestCode == TASKS && resultCode == RESULT_OK ){
        	ArrayList<UserFenPai> tasks = (ArrayList<UserFenPai>) data
					.getSerializableExtra("select_list");
        	project.Tasks.clear();
        	project.Tasks.addAll(tasks);
        	Log.d(TAG, "retTask count "+tasks.size()+"-project count"+project.Tasks.size());
        	if(project.Tasks.size() == 0){
    			mTaskNum.setVisibility(View.INVISIBLE);
    		}else{
    			mTaskNum.setVisibility(View.VISIBLE);
    			mTaskNum.setText(project.Tasks.size()+"");
    		}
        }
        //关连相关日程
        if(requestCode == SCHEDULE && resultCode == RESULT_OK ){
        	String[] strings = {};
        	String scheduleString = data.getStringExtra(RelativeScheduleActivity.SCHEDULE);
        	if(scheduleString != null){
        		strings = string2array(scheduleString);
        	}
        	project.Line_Schedule = scheduleString;
        	if(strings.length == 0){
    			mScheduleNum.setVisibility(View.INVISIBLE);
    		}else{
    			mScheduleNum.setVisibility(View.VISIBLE);
    			mScheduleNum.setText(strings.length + "");
    		}
        }
        //知识库
        if(requestCode == REPOSITORY && resultCode == RESULT_OK ){
        	ArrayList<Files> fileRets = (ArrayList<Files>) data
					.getSerializableExtra("select_list");
        	CommonUtil.filterFiles(files, fileRets);
        	project.Line_File = files2string(files);
        	if(files.size() == 0){
    			mRepositoryNum.setVisibility(View.INVISIBLE);
    		}else{
    			mRepositoryNum.setVisibility(View.VISIBLE);
    			mRepositoryNum.setText(files.size()+"");
    		}
        }
    } 

	private void initView(){
		Calendar calendar = Calendar.getInstance(); 
        mStartPopupWindow = new TimePickerView(this, Type.ALL);//时间选择器
        mStartPopupWindow.setTitle(getResources().getString(R.string.beg_time));
        mStartPopupWindow.setCancelable(true);
        mStartPopupWindow.setOnTimeSelectListener(new OnTimeSelectListener() { //时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
            	dateDiaplay(date);
            }
        });
		//当获取到project不为空时表示处于编辑状态，project为空时表示添加
		responserAdapter = new ResponserAdapter(this);
		project = (UserProject) getIntent().getSerializableExtra(
				EDIT_PROJECT);
		if(project != null){
			showData(project);
			mHeaderTitleTv.setText(getResources().getString(R.string.modify_project));
			Log.d(TAG, project.MainUser);
			showResponser(project.MainUser);
			
		}else{
			project = new UserProject();
			project.ID = 0;
			project.Tasks = new ArrayList<UserFenPai>();
			mHeaderTitleTv.setText(getResources().getString(R.string.new_project));
			mTaskNum.setVisibility(View.INVISIBLE);
			mScheduleNum.setVisibility(View.INVISIBLE);
			mRepositoryNum.setVisibility(View.INVISIBLE);
		}
		presenter = new ProjectPresenter(this);
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.cm_header_btn_left:
					onBackPressed();
					break;
				case R.id.cm_header_tv_right:
					if(getUserData()){
						presenter.addProject(project);
						dialog = createLoadingDialog(
								getResources().getString(R.string.progress_tip));
						dialog.show();
					}
					break;
				case R.id.et_projectmodify_name:
					mName.setFocusable(true);
					mName.setFocusableInTouchMode(true);
					mName.requestFocus();
					mName.findFocus();
					break;
				case R.id.et_projectmodify_description:
					mDescription.setFocusable(true);
					mDescription.setFocusableInTouchMode(true);
					mDescription.requestFocus();
					mDescription.findFocus();
					break;
				case R.id.tv_projectmodify_state_tip:
					ActionSheetDialog dialog = new ActionSheetDialog(ModifyProjectActivity.this).builder();
					dialog.addSheetItem("普通", null, new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							projectState = 0;
							mStateTip.setText("普通");
							mStateTip.setTextColor(getResources().getColor(R.color.cm_text));
						}
					});
					dialog.addSheetItem("紧急", null, new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							projectState = 1;
							mStateTip.setText("紧急");
							mStateTip.setTextColor(getResources().getColor(R.color.cm_text));
						}
					});
					dialog.addSheetItem("非常紧急", null, new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							projectState = 2;
							mStateTip.setText("非常紧急");
							mStateTip.setTextColor(getResources().getColor(R.color.cm_text));
						}
					});
					dialog.show();
					break;
				case R.id.tv_projectmodify_startTime:
					date_type = 0;
					HelpUtil.hideSoftInput(ModifyProjectActivity.this, mStartTime);
					mStartPopupWindow.show();
					break;
				case R.id.tv_projectmodify_endTime:
					date_type = 1;
					HelpUtil.hideSoftInput(ModifyProjectActivity.this, mEndTime);
					mStartPopupWindow.show();
					break;
				case R.id.ll_projectmodify_add:
					Intent addIntent = new Intent(ModifyProjectActivity.this,
							ContactSelectActivity.class);
					addIntent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, 
							ContactSelectActivity.MULTI_SELECT);
					users = responserAdapter.getArrayUser();
					addIntent.putExtra("select_list", users);
					startActivityForResult(addIntent,ContactSelectActivity.MULTI_SELECT);
					break;
				case R.id.ll_projectmodify_relative_task:
					Intent taskIntent = new Intent(ModifyProjectActivity.this,RelativeTaskActivity.class);
					
					StringBuilder sBuilder = new StringBuilder();
					if(project.Tasks != null && project.Tasks.size() > 0){
						sBuilder.append(project.Tasks.get(0).ID);
						for(int i = 1;i<project.Tasks.size();i++){
							sBuilder.append(",").append(project.Tasks.get(i).ID);
						}
					}
					taskIntent.putExtra("task_ids", sBuilder.toString());
                    Log.d(TAG,"onClick()->task_ids: " + sBuilder.toString());
					startActivityForResult(taskIntent, TASKS);
					break;
				case R.id.ll_projectmodify_active_schedule:
					Intent scheduleIntent = new Intent(ModifyProjectActivity.this,RelativeScheduleActivity.class);
					scheduleIntent.putExtra(RelativeScheduleActivity.SCHEDULE, project.Line_Schedule);
					startActivityForResult(scheduleIntent, SCHEDULE);
					break;
				case R.id.ll_projectmodify_repository:
					Intent repositoryIntent = new Intent(ModifyProjectActivity.this, FileSelectActivity.class);
					repositoryIntent.putExtra(FileSelectActivity.EXTRA_SELECT_IDS, project.Line_File);
					startActivityForResult(repositoryIntent, REPOSITORY);
					break;
				default:
					break;
				}
			}
		};
		mHeaderFinishBtn.setText(getResources().getString(R.string.finish));
		mHeaderFinishBtn.setVisibility(View.VISIBLE);
		mHeaderBackBtn.setOnClickListener(onClickListener);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderFinishBtn.setOnClickListener(onClickListener);
		mStateTip.setOnClickListener(onClickListener);
		mStartTime.setOnClickListener(onClickListener);
		mEndTime.setOnClickListener(onClickListener);
		mAddMemberBtn.setOnClickListener(onClickListener);
		mRelativeTaskBtn.setOnClickListener(onClickListener);
		mActiveSchedule.setOnClickListener(onClickListener);
		mRepository.setOnClickListener(onClickListener);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void loadProjectList(ProjectAdapter adapter) {
		
	}

	@Override
	public void showAddSucessTip() {
		dialog.dismiss();
		if(project.ID == 0){
			ToastUtil.showToast(this, 
					getResources().getString(R.string.add_success), 
					R.drawable.tishi_ico_gougou);
		}else{
			ToastUtil.showToast(this, 
					getResources().getString(R.string.modify_success),
					R.drawable.tishi_ico_gougou);
			setResult(RESULT_OK, getIntent().putExtra(EDIT_PROJECT, project));
		}
		Intent intentBroadCast = new Intent();
        intentBroadCast.putExtra("modify",MODIFY);
		intentBroadCast.setAction(SummaryFragment.PROJECT_REFRESH);
		sendBroadcast(intentBroadCast);
		onBackPressed();
	}

	@Override
	public void showAddFaildTip() {
		dialog.dismiss();
		if(project.ID == 0){
			ToastUtil.showToast(this, 
					getResources().getString(R.string.add_fail));
		}else{
			ToastUtil.showToast(this, 
					getResources().getString(R.string.modify_fail));
		}
	}
	//获取用户数据
	private boolean getUserData(){
		project.Name = (mName.getText().toString());
		project.Mark = (mDescription.getText().toString());
		project.BeginTime = (mStartTime.getText().toString());
		project.EndTime = (mEndTime.getText().toString());
		project.Color = (1);
		Log.d(TAG, "setMainUser"+members2string());
		project.MainUser = (members2string());
		project.KeyInfo = ("Pro");
		project.Color = (projectState);
		if(TextUtils.isEmpty(project.Name)){
			ToastUtil.showToast(this, 
					getResources().getString(R.string.input_prj_name));
			return false;
		}
		if(TextUtils.isEmpty(project.Mark)){
			ToastUtil.showToast(this, 
					getResources().getString(R.string.input_prj_description));
			return false;
		}
		if("请选择开始时间".equals(project.BeginTime)){
			ToastUtil.showToast(this, 
					getResources().getString(R.string.input_prj_begtime));
			return false;
		}
		if("请选择结束时间".equals(project.EndTime)){
			ToastUtil.showToast(this, 
					getResources().getString(R.string.input_prj_endtime));
			return false;
		}
		if(project.MainUser.equals("[]") || TextUtils.isEmpty(project.MainUser)){
			ToastUtil.showToast(this, 
					getResources().getString(R.string.input_prj_responder));
			return false;
		}

		return true;
		
	}
	//显示信息
	private void showData(UserProject prj){
		mName.setText(prj.Name);
		mDescription.setText(prj.Mark);
		mStartTime.setText(prj.BeginTime);
		mStartTime.setTextColor(getResources().getColor(R.color.cm_text));
		mEndTime.setText(prj.EndTime);
		mEndTime.setTextColor(getResources().getColor(R.color.cm_text));
		mState.setText(prj.Color+"");
		switch (prj.Color) {
		case 0:
			mStateTip.setText("普通");
			break;
		case 1:
			mStateTip.setText("紧急");
			break;
		case 2:
			mStateTip.setText("非常紧急");
			break;

		default:
			mStateTip.setText(prj.Color+"");
			break;
		}
		mStateTip.setTextColor(getResources().getColor(R.color.cm_text));
		//显示相关任务数量
		if(prj.Tasks.size() == 0){
			mTaskNum.setVisibility(View.INVISIBLE);
		}else{
			mTaskNum.setVisibility(View.VISIBLE);
			mTaskNum.setText(prj.Tasks.size()+"");
		}
		//显示相关日程数量 
		if(prj.Line_Schedule == null 
				|| prj.Line_Schedule.equals("")
				|| prj.Line_Schedule.equals("[]")){
			mScheduleNum.setVisibility(View.INVISIBLE);
		}else{
			mScheduleNum.setVisibility(View.VISIBLE);
			mScheduleNum.setText(string2array(project.Line_Schedule).length + "");
		}
		//显示知识库数量
		if(prj.Line_File == null 
				|| prj.Line_File.equals("")
				|| prj.Line_File.equals("[]")){
			mRepositoryNum.setVisibility(View.INVISIBLE);
		}else{
			mRepositoryNum.setVisibility(View.VISIBLE);
			mRepositoryNum.setText(string2array(project.Line_File).length + "");
		}
	}
	
	//显示负责人
	private void showResponser(String userString){
		String[] strings;
//		userString = userString.substring(1,userString.length()-1);
		strings = userString.split(",");
		for(int i = 0;i<strings.length;i++){
			if(!strings[i].equals("")){
				Log.d(TAG, strings[i]);
				users.add(EMWApplication.personMap.get(Integer.valueOf(strings[i])));
			}
		}
		responserAdapter.setArrayUser(users,true);
		mResponserListView.setAdapter(responserAdapter);
		CommonUtil.setListViewHeightBasedOnChildren(mResponserListView);
	}
	
	//负责人转成id拼接的字符串
	private String members2string(){
		users.clear();
		users.addAll(responserAdapter.getArrayUser());
		StringBuilder sb = new StringBuilder();
//		sb.append("[");
		for(int i = 0;i<users.size();i++){
			sb.append(users.get(i).ID);
			if(i != users.size()-1){
				sb.append(",");
			}
		}
//		sb.append("]");
		Log.d(TAG, "av"+sb.toString());
		return sb.toString().trim();
	}
	
	//把知识库集合id转成id字符串
	private String files2string(ArrayList<Files> files){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i = 0;i<files.size();i++){
			sb.append(files.get(i).ID);
			if(i != files.size()-1){
				sb.append(",");
			}
		}
		sb.append("]");
		Log.d(TAG, "av"+sb.toString());
		return sb.toString().trim();
	}
	
	//设置时间显示
	private void dateDiaplay(Date date){  
		SimpleDateFormat f = new SimpleDateFormat(
				getResources().getString(R.string.timeformat4),Locale.CHINA);
        if(date_type == 0){
        	mStartTime.setText(f.format(date)); 
        	mStartTime.setTextColor(getResources().getColor(R.color.cm_text));
        }else{
        	try {
				Date d = f.parse(mStartTime.getText().toString());
				if(d.getTime()<date.getTime()){
					mEndTime.setText(f.format(date));
					mEndTime.setTextColor(getResources().getColor(R.color.cm_text));
				}else{
					ToastUtil.showToast(this, 
							getResources().getString(R.string.endtime_less_begtime));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
    }

	//将以字符串按","分割成数组
	private String[] string2array(String str){
		String[] strings;
    	str =str.substring(1,str.length()-1);
		strings = str.split(",");
		return strings;
	}

	@Override
	public void onNetworkError() {
	}

	@Override
	protected void onPause() {
		super.onPause();
		mName.clearFocus();
		mDescription.clearFocus();
	}
}
