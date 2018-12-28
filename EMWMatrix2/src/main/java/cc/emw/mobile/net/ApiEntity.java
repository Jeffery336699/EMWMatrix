package cc.emw.mobile.net;

import java.io.Serializable;
import java.util.List;

public class ApiEntity {
	public static class TableField implements Serializable{
		public int DBType; //DBType
		public String ID;
		public int Length;
		public String Name;
		public int Width;
		public Object Element;
	}
	public static class ExportParams implements Serializable{
		public int ExportType; //ExportType
		public int TableId;
		public int PageIndex;
		public int PageSize;
		public int RowCount;
	}
	public static class UploadResult implements Serializable{
		public String NewName;
		public String SourceName;
		public int FileLength;
		public String Url;
	}
	public static class UserAxis implements Serializable{
		public int ID;
		public String Axis;
		public String Name;
		public String Image;
	}
	public static class Dept implements Serializable{
		public int ID;
		public String Code;
		public String Name;
		public int ParentId;
	}
	public static class UserInfo implements Serializable{
		public int ID;
		public String Name;
		public String Image;
		public String Job;
		public String DeptName;
		public boolean IsFollow;
		public int DeptID;
		public boolean IsOnline;
		public String Code;
		public String Phone;
		public String Password;
		public int UserType; //UserTypes
		public String CompanyCode;
		public String Email;
		public String VoipCode;
		public String VoipPwd;
		public String DeviceToken;
		public String EmailSignText;
	}
	public static class Role implements Serializable{
		public int ID;
		public String Name;
		public String Image;
		public String Email;
		public int Type; //RoleTypes
	}
	public static class ChatRecord implements Serializable{
		public int UserID;
		public int ReceiverID;
		public int Type; //ChatType
		public ApiEntity.Message Message;
		public int UnReadCount;
	}
	public static class File implements Serializable{
		public String FileName;
		public String Url;
		public int FileId;
		public int Length;
		public int CreateUser;
	}
	public static class FileDown implements Serializable{
		public int Fid;
		public int UserId;
		public String UserName;
		public String CreateTime;
	}
	public static class Message implements Serializable{
		public int ID;
		public String Content;
		public int UserID;
		public int SenderID;
		public int ReceiverID;
		public int Type; //MessageType
		public String CompanyCode;
		public String CreateTime;
		public int GroupID;
	}
	public static class UserTalkerShare implements Serializable{
		public int NoteID;
		public String UserName;
		public String Content;
	}
	public static class UserNote implements Serializable{
		public int ID;
		public String Content;
		public String Property;
		public int UserID;
		public String UserName;
		public String UserImage;
		public int AddType; //UserNoteAddTypes
		public int SendType; //UserNoteSendTypes
		public int PID;
		public int RevCount;
		public String CreateTime;
		public int MessageCount;
		public int EnjoyCount;
		public boolean IsEnjoy;
		public int Type;
		public int DevType;
		public List<ApiEntity.UserNoteRevs> Revs;
		public List<ApiEntity.NoteRole> Roles;
		public boolean IsSendMail;
		public int ToUserId;
		public int TopId;
		public int TypeId;
		public int ShareCount;
		public ApiEntity.Role UserIdInfo;
		public List<ApiEntity.Role> ToUserIdInfo;
		public int ProjectID;
	}
	public static class NoteRole implements Serializable{
		public int Type; //NoteRoleTypes
		public int ID;
		public String Name;
		public String Image;
	}
	public static class UserNoteRevs implements Serializable{
		public int ID;
		public String Content;
		public int PID;
		public String CreateTime;
		public String Title;
		public String Property;
	}
	public static class UserSchedule implements Serializable{
		public int ID;
		public int UserID;
		public int State;
		public String Title;
		public String StartTime;
		public String OverTime;
		public int Type;
		public int Priority;
		public int Allday;
		public int Color;
		public String Remark;
		public String NotePriority;
		public String Line_Task;
		public String Line_Project;
		public String Line_File;
		public String Line_Group;
		public int AHEAD_MINUTE;
		public int ISCALL;
		public int REPEATTYPE;
		public String REPEATENDTIME;
		public int REPEATHZ;
		public String REPEATWEEKVAL;
		public int NoteId;
		public int SType;
		public int IsJobSync;
		public String RepeatTimeVal;
		public int RepeatMonthDayVal;
		public String RepeatYearDayVal;
		public List<ApiEntity.NoteRole> NoteRoles;
		public String NoteContent;
	}
	public static class Calendar implements Serializable{
		public String end;
		public String start;
		public String error;
		public boolean issort;
		public List<Object[]> events;
	}
	public static class Result implements Serializable{
		public boolean IsSuccess;
		public String Msg;
		public int Data;
	}
	public static class UserFenPai implements Serializable{
		public int ID;
		public int ProjectId;
		public int State;
		public String Title;
		public String Mark;
		public String StartTime;
		public String FinishTime;
		public String CreateTime;
		public int Creator;
		public int Color;
		public String MainUser;
		public String MoreUser;
		public int PID;
		public int Tier;
		public int Depends;
		public List<ApiEntity.UserFenPai> Tasks;
		public String Files;
		public String NotePriority;
		public int TaskType;
		public int Yxj;
		public String Progress;
		public int FlowState;
		public String Line_Schedule;
		public String Line_Group;
		public int IsJobSync;
		public String Roles;
		public int Type;
		public List<ApiEntity.Role> MainUserList;
		public List<ApiEntity.Role> MoreUserList;
		public String NoteRoles;
		public String NoteContent;
	}
	public static class UserProject implements Serializable{
		public int ID;
		public String Name;
		public int Creator;
		public String CreateTime;
		public String Mark;
		public String KeyInfo;
		public String BeginTime;
		public String EndTime;
		public String MainUser;
		public List<ApiEntity.Role> MainUserList;
		public String MainUserName;
		public int Color;
		public List<ApiEntity.UserFenPai> Tasks;
		public String Progress;
		public String Line_Schedule;
		public String Line_File;
		public String Line_Group;
		public int TeamId;
		public int GroupId;
		public int FolderId;
	}
	public static class UserSprint implements Serializable{
		public int ID;
		public String Name;
		public int Creator;
		public String CreateTime;
		public int Statu;
		public String Content;
	}
	public static class UserPlan implements Serializable{
		public int ID;
		public int Nid;
		public int Creator;
		public String CreateTime;
		public String Name;
		public int Type;
		public String Mark;
		public String BeginTime;
		public String EndTime;
		public String NotePriority;
		public int FinishState;
		public int IsJobSync;
		public String NoteRoles;
	}
	public static class TaskReply implements Serializable{
		public int ID;
		public int ToUserID;
		public int ParentID;
		public int TaskId;
		public int Creator;
		public String CreateTime;
		public String Content;
		public List<ApiEntity.TaskReply> Replys;
		public List<ApiEntity.Role> CreatorInfo;
		public List<ApiEntity.Role> ToUserIdInfo;
	}
	public static class UserFeedBack implements Serializable{
		public int ID;
		public String Content;
		public int Creator;
		public String CreateTime;
		public int FeedType;
	}
	public static class UserMark implements Serializable{
		public int ID;
		public int UserId;
		public int Creator;
		public String Name;
		public String CreateTime;
	}
	public static class UserTeam implements Serializable{
		public int ID;
		public int Creator;
		public String Name;
		public String CreateTime;
		public int Type;
		public List<ApiEntity.Role> Users;
		public String Line_File;
	}
	public static class TrackFile implements Serializable{
		public int ID;
		public int UserId;
		public String UserName;
		public List<ApiEntity.File> FileInfo;
		public int Type;
		public String CreateTime;
	}
	public static class TeamCheckIn implements Serializable{
		public int ID;
		public int Creator;
		public String CreateTime;
		public String Content;
		public String MainUser;
		public List<ApiEntity.Role> MainUserList;
		public String ActiveTime;
		public int IsCall;
		public int TeamId;
	}
	public static class Column implements Serializable{
		public String Name;
		public String Alias;
		public int FieldID;
		public String DataType;
	}
	public static class TableDataSource implements Serializable{
		public String Name;
		public int TableID;
		public String ReferenceName;
		public List<ApiEntity.Column> Columns;
	}
	public static class Navigation implements Serializable{
		public int ID;
		public String Name;
		public int STATE;
		public String ICON;
		public int TemplateID;
		public int TYPE;
		public int PID;
		public int PAGEID;
		public String Memo;
		public String Parameter;
		public String URL;
	}
	public static class MailSetting implements Serializable{
		public String SendHost;
		public int SendPort;
		public String ReceiveHost;
		public int ReceivePort;
		public String Address;
		public String DisplayName;
		public String Password;
		public boolean EnableSSL;
		public boolean UseSystem;
	}
	public static class MailMessage implements Serializable{
		public String From;
		public List<String> To;
		public String Subject;
		public String Body;
		public List<String> CC;
		public List<String> Bcc;
		public List<String> Attachments;
	}
	public static class ChatterGroup implements Serializable{
		public int ID;
		public String Name;
		public String Memo;
		public int Type; //ChatterGroupTypes
		public boolean IsAddIn;
		public int Count;
		public String CreateTime;
		public String CreateUserName;
		public int CreateUser;
		public String Image;
		public String Line_File;
		public int TeamType;
		public List<ApiEntity.UserInfo> Users;
		public int ProjectId;
		public int Parent;
	}
	public static class GroupMember implements Serializable{
		public int ID;
		public int UserID;
		public int GroupID;
	}
	public static class Files implements Serializable{
		public int ID;
		public int Type;
		public int Creator;
		public String T_Creator;
		public String CreateTime;
		public int TID;
		public int RID;
		public int NID;
		public String Name;
		public String Url;
		public long Length;
		public String Content;
		public boolean IsDelete;
		public String UpdateTime;
		public int UpdateUser;
		public int ParentID;
		public String FilePath;
		public int FilePower; //UserFilePowerType
		public int ProjectId;
	}
	public static class UserFilePower implements Serializable{
		public int ID;
		public int FID;
		public String Name;
		public int Type; //NoteRoleTypes
		public int Power; //UserFilePowerType
	}
	public static class FlowInfo implements Serializable{
		public int ID;
		public int RecordID;
		public int UserID;
		public int FlowModuleID;
		public String Title;
		public String StartTime;
		public int State;
		public int NodeID;
	}
	public static class FlowHistory implements Serializable{
		public int ID;
		public int FlowID;
		public int RecordID;
		public int UserID;
		public String UserName;
		public int AgentID;
		public String LineName;
		public int LineID;
		public String Content;
		public int NodeID;
		public String NodeName;
		public String OperTime;
		public String NextUsers;
	}
	public static class FlowState implements Serializable{
		public int ID;
		public int FlowID;
		public int RecordID;
		public int User;
		public int AgentID;
		public String NextUsers;
		public String NextUserNames;
		public String Title;
		public int NodeID;
		public int LineID;
		public int NextRecord;
		public String Content;
		public List<String> RecordState;
		public int NextNode;
		public int PageID;
	}
	public static class ApiType implements Serializable{
		public String Name;
		public String Path;
		public List<ApiEntity.ApiMethod> Methods;
	}
	public static class ApiMethod implements Serializable{
		public String Name;
		public String NameDesc;
		public String Path;
		public String HttpMethod;
		public String HttpMethodDesc;
		public List<ApiEntity.ApiParameter> ParaList;
	}
	public static class ApiParameter implements Serializable{
		public String Name;
		public String NameDesc;
		public String Type;
		public String TypeDesc;
		public String Desc;
	}
	public static class ApiDesc implements Serializable{
		public String Name;
		public String Desc;
		public List<ApiEntity.ApiParameter> ParaList;
		public String Return;
	}
	public static class Condition implements Serializable{
		public String Left;
		public String Right;
		public int Oper; //ConditionOpers
		public String Desc;
		public List<ApiEntity.Condition> Conditions;
	}
	public static class DataSource implements Serializable{
		public List<ApiEntity.UserTable> Tables;
		public ApiEntity.UserTable MainTable;
		public List<ApiEntity.TableRelation> Relations;
		public List<ApiEntity.Condition> Conditions;
		public String OrderBy;
	}
	public static class TableRelation implements Serializable{
		public String TargetTable;
		public int Type; //RelationType
		public List<ApiEntity.Condition> Conditions;
	}
	public static class GroupPower implements Serializable{
		public int GroupId;
		public int FuncId;
		public int Type; //GroupPowerTypes
		public int OId;
	}
	public static class Language implements Serializable{
		public int ID;
		public String Name;
	}
	public static class PagePower implements Serializable{
		public int ID;
		public String Name;
		public String Memo;
		public int Language; //Languages
		public int TemplateID;
		public String Setting;
		public int PageID;
	}
	public static class Group implements Serializable{
		public int ID;
		public String Name;
		public String Memo;
		public int State;
		public int Parent;
		public int TemplateID;
	}
	public static class GroupEntity implements Serializable{
		public int ID;
		public String Name;
		public String Memo;
		public int TemplateID;
		public int State;
		public int Langague;
		public int Parent;
	}
	public static class PowerSetting implements Serializable{
		public int ID;
		public String Name;
		public String Memo;
		public int PID;
		public String SETTING;
	}
	public static class SysModule implements Serializable{
		public int ID;
		public String Name;
		public String URL;
	}
	public static class Template implements Serializable{
		public int ID;
		public String Name;
		public String Memo;
		public int Language; //Languages
		public int Creator;
		public String CreateTime;
		public String CreatorName;
		public int Modifier;
		public String ModifierName;
		public String ModifyTime;
		public int VER;
	}
	public static class Resource implements Serializable{
		public int ID;
		public int TemplateID;
		public String Name;
		public String Memo;
		public int TableId;
		public int MainTable;
		public int Type; //ResourceTypes
		public String Content;
		public int Creator;
		public String CreateTime;
		public String CreatorName;
		public int Modifier;
		public String ModifierName;
		public String ModifyTime;
		public int Language; //Languages
	}
	public static class UserFile implements Serializable{
		public int ID;
		public String Name;
		public String Memo;
		public int UserFileType; //UserFileTypes
		public int Language; //Languages
		public String Content;
		public int TableID;
		public boolean IsMain;
		public int CreateUser;
		public String CreateUserName;
		public String CreateTime;
		public int LastEditUser;
		public String LastEditUserName;
		public String LastEditTime;
		public int TemplateID;
	}
	public static class UserTable implements Serializable{
		public String TableName;
		public int ID;
		public String Name;
		public String Image;
		public int Type; //UserTableTypes
		public int InfoID;
		public String Memo;
		public List<ApiEntity.UserField> Fields;
		public int Language; //Languages
		public List<Integer> RecordID;
		public String AliasName;
		public int SeqType; //SeqType
		public int Power; //UserPowers
		public boolean IsHasCreator;
		public boolean IsHasState;
		public boolean IsHasDesc;
		public boolean IsHasModifier;
		public ApiEntity.UserField RelationField;
	}
	public static class UserView implements Serializable{
		public int TableID;
		public int ID;
	}
	public static class SystemInfo implements Serializable{
		public int ID;
		public String Name;
		public String Memo;
		public List<ApiEntity.UserTable> Tables;
		public int Language; //Languages
	}
	public static class UserField implements Serializable{
		public int ID;
		public String FieldName;
		public String AliasName;
		public String Name;
		public String Memo;
		public int DBType; //DBType
		public int Length;
		public int AutoCode;
		public int Power; //UserPowers
		public int InputType; //InputTypes
		public boolean IsPrimary;
		public int ReferenceTable;
		public int ValueField;
		public int TextField;
		public boolean IsAllowNull;
		public List<ApiEntity.FieldValue> FieldValues;
		public int InputFormat; //InputFormats
		public int Accuracy;
		public boolean IsMonitor;
		public ApiEntity.UserTable Table;
		public List<Object> Values;
		public String Default;
	}
	public static class FieldValue implements Serializable{
		public String Text;
		public int Value;
		public boolean IsDefault;
		public boolean IsDisabled;
		public boolean IsDelete;
	}
	public static class RelationSetting implements Serializable{
		public String MainTable;
		public int MainTableID;
		public int MainFieldID;
		public String MainField;
		public int SubTableID;
		public String SubTable;
		public int SubFieldID;
		public String SubField;
		public String RelationTable;
		public String RelationMainField;
		public String RelationSubField;
		public int MainRecord;
		public int Type;
		public boolean RelDelete;
	}
	public static class TemplateBuyRecord implements Serializable{
		public int ID;
		public String CompanyCode;
		public String TemplateListID;
		public int NumForYear;
		public int UserCount;
		public String BuyTime;
		public int BuyState;
		public List<ApiEntity.Template> Templates;
	}
	public static class Company implements Serializable{
		public int ID;
		public String CompanyName;
		public String CompanyCode;
		public String BusinessLicenseNum;
		public String Address;
		public String UserEmail;
		public String UserName;
		public String UserPhone;
//		public ApiEntity.Guid EmailGuid;
		public String UserJob;
		public int STATE;
		public int ALLOWUSER;
		public String CompanyPw;
		public String Countries;
		public String ProvinceOrstate;
		public String City;
		public String County;
		public String ProductCategory;
		public String CompanySize;
	}
	public static class AreaInfo implements Serializable{
		public int ID;
		public String Code;
		public String Name;
		public String Memo;
		public int Type; //AreaType
		public int ParentID;
		public ApiEntity.AreaInfo Parent;
		public List<ApiEntity.AreaInfo> Children;
	}
}