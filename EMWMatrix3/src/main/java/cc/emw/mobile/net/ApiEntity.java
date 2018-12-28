package cc.emw.mobile.net;

import java.io.Serializable;
import java.util.List;

public class ApiEntity {
    public static class TableField implements Serializable {
        public int DBType; //DBType
        public String ID;
        public int Length;
        public String Name;
        public int Width;
        public Object Element;
    }

    public static class ExportParams implements Serializable {
        public int ExportType; //ExportType
        public int TableId;
        public int PageIndex;
        public int PageSize;
        public int RowCount;
    }

    public static class UploadResult implements Serializable {
        public String NewName;
        public String SourceName;
        public int FileLength;
        public String Url;
        public ApiEntity.Files FileInfo;
    }

    public static class UserAxis implements Serializable {
        public int ID;
        public String Axis;
        public String Name;
        public String Image;
    }

    public static class Dept implements Serializable {
        public int ID;
        public String Code;
        public String Name;
        public int ParentId;
    }

    public static class UserInfo implements Serializable {
        public int ID;
        public int IdentityID;
        public String Name;
        public String Image;
        public String BackImage;
        public String Job;
        public int Sex;
        public String Birthday;
        public int Age;
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
        public String Axis;
        public int InitState; //UserInitState
        public String JobExperience;
        public String ClassSkill;
        public String College;
        public String HighSchool;
        public List<ApiEntity.UserSetting> UserSettings;
        public boolean IsSharePos;
        public String WxpayRQCode;//微信二维码地址
        public String AlipayRQCode;//支付宝二维码地址
        public String RongYunToken;
        public List<ApiEntity.UserInfo> SameUserList;
    }

    public static class TalkerUserInfo implements Serializable {
        public int ID;
        public String Name;
        public String Image;
        public String Job;
        public boolean IsFollow;
        public boolean IsOnline;
        public String CompanyCode;
    }

    public static class Role implements Serializable {
        public int ID;
        public String Name;
        public String Image;
        public String Email;
        public String Job;
        public int DeptId;
        public String DeptName;
        public int Type; //RoleTypes
    }

    public static class UserLabel implements Serializable {
        public int ID;
        public String Name;
        public int Creator;
        public String CreateTime;
    }

    public static class UserSetting implements Serializable {
        public int UserID;
        public String Key;
        public String Value;
        public int UType; //UserSettingTypes
        public String UpdateTime_;
    }

    public static class UserRail implements Serializable {
        public int ID;
        public String Address;
        public String Axts;
        public int Creator;
        public int Type;
        public int Radius;
        public String CreateTime;
    }

    public static class UserRailManage implements Serializable {
        public int ID;
        public int RID;
        public int Creator;
        public int Type;
        public String CreateTime;
        public String Address;
    }

    public static class PubUserContacts implements Serializable {
        public int ID;
        public int UserID;
        public int ConID;
        public int GroupID;
        public ApiEntity.UserInfo ConUser;
        public ApiEntity.PubConGroups GroupElem;
        public String Mome;
    }

    public static class PubConGroups implements Serializable {
        public int ID;
        public String Name;
        public int UserID;
    }

    public static class PubConApply implements Serializable {
        public int ID;
        public String Image;
        public String Name;
        public String CompanyCode;
        public int ConID;
        public int State; //ConApplyState
        public int Creator;
        public String CreatTime;
        public int SameCount;
    }

    public static class ChatRecord implements Serializable {
        public int UserID;
        public int ReceiverID;
        public int Type; //ChatType
        public ApiEntity.Message Message;
        public int UnReadCount;
    }

    public static class File implements Serializable {
        public String FileName;
        public String Url;
        public int FileId;
        public int Length;
        public int CreateUser;
    }

    public static class FileDown implements Serializable {
        public int Fid;
        public int UserId;
        public String UserName;
        public String CreateTime;
        public String FileName;
        public String FileUrl;
    }

    public static class Message implements Serializable, Comparable<Message> {
        public int ID;
        public String Content;
        public int UserID;
        public int SenderID;
        public int ReceiverID;
        public int Type; //MessageType
        public String CompanyCode;
        public String CreateTime;
        public int GroupID;
        public int BusType; //BusTypes
        public int IsNew;
        public int topIdOut;
        public ApiEntity.Role User;
        public ApiEntity.Role Receiver;
        public ApiEntity.Role Sender;
        public ApiEntity.Role Group;
        public int state;

        @Override
        public int compareTo(Message another) {
            return -(this.topIdOut - another.topIdOut);
        }
    }

    public static class UserTalkerShare implements Serializable {
        public int NoteID;
        public String UserName;
        public String Content;
    }

    public static class UserNote implements Serializable {
        public int ID;
        public String Content;
        public String Property;
        public String AddProperty;
        public int UserID;
        public String UserName;
        public String UserImage;
        public int AddType; //UserNoteAddTypes
        public int SendType; //UserNoteSendTypes
        public int PID;
        public int RevCount;
        public List<ApiEntity.UserNote> RevInfo;
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
        public ApiEntity.TalkerUserInfo UserIdInfo;
        public ApiEntity.TalkerUserInfo ToUserIdInfo;
        public int ProjectID;
        public List<ApiEntity.UserNote> ShareInfo;
        public int RecordCount;
        public boolean IsVote;
        public List<ApiEntity.TalkerUserInfo> EnjoyList;
        public int ShareId;
        public int RecordID;
        public int GroupID;

    }

    public static class NoteRole implements Serializable {
        public int Type; //NoteRoleTypes
        public int ID;
        public String Name;
        public String Image;
    }

    public static class UserNoteRevs implements Serializable {
        public int ID;
        public String Content;
        public int PID;
        public String CreateTime;
        public String Title;
        public String Property;
    }

    public static class UserSchedule implements Serializable {
        public int ID;
        public int SendType;
        public int UserID;
        public String T_Creator;
        public String T_CreatorImage;
        public int State;
        public String Title;
        public String StartTime;
        public String OverTime;
        public String AwakeTime;
        public int Type;
        public int Priority;
        public int Allday;
        public int Color;
        public String Remark;
        public String NotePriority;
        public String Line_Task;
        public String Line_Project;
        public String Line_File;
        public List<String> FileName;
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
        public String NoteAddPriority;
        public int NoteType;
        public int NoteAddType;
        public int RID;
        public String Label;
        public String Place;
        public String MainUser;
        public String ImagePath;//用于Chat模块创建带有约会实际项目的字段，保存图片的URL。  非必须要传入值。
        public int Launcher;
        public int Receiver;
        public int Service;
        public String Phone;
        public int PhoneType;
        public String Spatial;
        public String Customer;
        public String Resources;
        public String MustActor;
        public String SuggestActor;
        public String TaskUser;
        public String CopySender;
        public String T_Customer;
        public String T_Resources;
        public String T_Service;
        public String T_Spatial;
        public String T_MustActor;
        public String T_SuggestActor;
        public String T_Receiver;
        public String T_Launcher;
        public String T_MainUser;
        public String T_CopySender;
        public ApiEntity.UserRail Rail;
        public int RailID;
        public int AppointPayType;//支付类型 ：0 请客，1AA制
        public String AppointPayVal;//AA制金额
    }

    public static class UserWork implements Serializable {
        public int ID;
        public int Creator;
        public String Title;
        public String StartTime;
        public String OverTime;
        public int NoticeType;
    }

    public static class Calendar implements Serializable {
        public String end;
        public String start;
        public String error;
        public boolean issort;
        public List<Object[]> events;
    }

    public static class Result implements Serializable {
        public boolean IsSuccess;
        public String Msg;
        public int Data;
    }

    public static class APIResult implements Serializable {
        public int State;
        public int Code;
        public String Msg;
        public int Total;
        public String Data;
    }

    public static class UserFenPai implements Serializable {
        public int ID;
        public int SendType;
        public ApiEntity.UserSprint SprintInfo;
        public ApiEntity.ChatterGroup TeamInfo;
        public int ProjectId;
        public ApiEntity.UserProject ProjectElem;
        public int State;//1是未开始，2进行中，3延迟，4已完成
        public String Title;
        public String Mark;
        public String StartTime;
        public String FinishTime;
        public String CreateTime;
        public String UpdateTime;
        public String FinishRate;
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
        public List<ApiEntity.UserInfo> MainUserList;
        public List<ApiEntity.UserInfo> MoreUserList;
        public String NoteRoles;
        public String NoteContent;
        public List<ApiEntity.TaskReply> TaskReply;
        public String TaskLabel;
        public String NewLabel;
        public String NoteAddPriority;
        public int NoteAddType;
        public int AllCount;
        public int FinishCount;
        public int ReplyCount;
        public ApiEntity.UserRail Rail;
        public int RailID;
        public int SprintId;
    }

    public static class UserProject implements Serializable {
        public int ID;
        public String Name;
        public int Creator;
        public int IsArchive;
        public List<ApiEntity.UserInfo> CreatorInfo;
        public String CreateTime;
        public String Mark;
        public String KeyInfo;
        public String BeginTime;
        public String EndTime;
        public String MainUser;
        public String Users;
        public List<ApiEntity.UserInfo> MainUserList;
        public List<ApiEntity.UserInfo> UsersList;
        public String MainUserName;
        public String UsersName;
        public int Color;
        public boolean IsLook;
        public List<ApiEntity.UserFenPai> Tasks;
        public String Progress;
        public String Line_Schedule;
        public String Line_File;
        public String Line_Group;
        public int TeamId;
        public int GroupId;
        public int FolderId;
        public int State;
        public int TaskCount;
        public int FinishTaskCount;
    }

    public static class UserSprint implements Serializable {
        public int ID;
        public String Name;
        public int Creator;
        public String CreateTime;
        public int Statu;
        public String Content;
        public String StartTime;
        public String OverTime;
    }

    public static class UserPlan implements Serializable {
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

    public static class TaskReply implements Serializable {
        public int ID;
        public int ToUserID;
        public int ParentID;
        public int RType;
        public int TaskId;
        public int Creator;
        public String CreateTime;
        public String Content;
        public List<ApiEntity.TaskReply> Replys;
        public List<ApiEntity.UserInfo> CreatorInfo;
        public List<ApiEntity.UserInfo> ToUserIdInfo;
        public int ReplyCount;
        public int LogType;
        public List<Integer> AtUsers;
        public int ContentType;
        public String AddProperty;
    }

    public static class UserFeedBack implements Serializable {
        public int ID;
        public String Content;
        public int Creator;
        public String CreateTime;
        public int FeedType;
        public String AddProperty;
    }

    public static class UserMark implements Serializable {
        public int ID;
        public int UserId;
        public int Creator;
        public String Name;
        public String CreateTime;
    }

    public static class UserTeam implements Serializable {
        public int ID;
        public int Creator;
        public String Name;
        public String CreateTime;
        public int Type;
        public List<ApiEntity.Role> Users;
        public String Line_File;
    }

    public static class TrackFile implements Serializable {
        public int ID;
        public int UserId;
        public String UserName;
        public List<ApiEntity.File> FileInfo;
        public int Type;
        public String CreateTime;
    }

    public static class UserOperate implements Serializable {
        public int ID;
        public int Creator;
        public String CreateTime;
        public String Content;
        public int CType;
        public int RID;
    }

    public static class TeamCheckIn implements Serializable {
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

    public static class Column implements Serializable {
        public String Name;
        public String Alias;
        public int FieldID;
        public String DataType;
    }

    public static class TableDataSource implements Serializable {
        public String Name;
        public int TableID;
        public String ReferenceName;
        public List<ApiEntity.Column> Columns;
    }

    public static class Navigation implements Serializable {
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
        public int PosType; //NavigationPos
        public int Sort;
        public int Visible;
        public String AliasName;
    }

    public static class CustomerPrice implements Serializable {
        public int ID;
        public int Direction;
        public List<ApiEntity.PriceArea> PriceAreas;
        public List<ApiEntity.PriceRange> PriceRanges;
        //		public ApiEntity.Double[][] Prices;
        public List<ApiEntity.PriceArea> DiscountAreas;
        public List<ApiEntity.PriceRange> DiscountRanges;
        //		public ApiEntity.Double[][] DiscountPrices;
    }

    public static class PriceArea implements Serializable {
        public List<ApiEntity.AreaInfo> Areas;
        public String Name;
        public String AreaText;
    }

    public static class PriceRange implements Serializable {
        public int CountType;
        //		public ApiEntity.Double EndWeight;
        public String GoodsType;
        //		public ApiEntity.Double StartWeight;
        public int Unit;
        public int Sort;
        public int ID;
    }

    public static class MailSetting implements Serializable {
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

    public static class MailMessage implements Serializable {
        public String From;
        public List<String> To;
        public String Subject;
        public String Body;
        public List<String> CC;
        public List<String> Bcc;
        public List<String> Attachments;
    }

    public static class ChatterGroup implements Serializable {
        public int ID;
        public String Name;
        public String Memo;
        public int Type; //ChatterGroupTypes
        public boolean IsAddIn;
        public int Count;
        public String CreateTime;
        public String CreateUserName;
        public String CreateUserImage;
        public int CreateUser;
        public String Image;
        public String Icon;
        public String Line_File;
        public int TeamType;
        public List<ApiEntity.UserInfo> Users;
        public List<Integer> OldUsers;
        public List<ApiEntity.UserInfo> MainUserInfo;
        public String MainUser;
        public int ProjectId;
        public int Parent;
        public ApiEntity.ChatterGroup ParentInfo;
        public int ProjectCount;
        public String TaskCountStr;
        public int TalkerCount;
        public int FileCount;
        public int MessageCount;
        public int Color;
        public int FileID;
        public int BackImageIndex;
        public List<Integer> MsgHideList;//针对该群个人用户做个性化设置：在非有人@当前用户的情况下有消息进入不在提示。
    }

    public static class GroupMember implements Serializable {
        public int ID;
        public int UserID;
        public int GroupID;
        public ApiEntity.UserInfo User;
    }

    public static class Files implements Serializable {
        public int ID;
        public int Type;
        public int SType;
        public int Creator;
        public ApiEntity.Role CreatorInfo;
        public String CompanyCode;
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
        public ApiEntity.Role UpdateUserInfo;
        public int ParentID;
        public String FilePath;
        public int FilePower;
        public int ProjectId;
        public int IsActive;
        public int OldId;
        public String Image_Creator;
        public int GroupId;
        public int Pro_Creator;
        public String Pro_MainUser;
        public String ThumbFileName;
    }

    public static class UserFilePower implements Serializable {
        public int ID;
        public int FID;
        public String Name;
        public String Image;
        public int Type; //NoteRoleTypes
        public int Power; //UserFilePowerType
    }

    public static class SheetDataList implements Serializable {
        public String RowId;
        public String ColumnId;
        public String Column;
        public String Type;
        public String Value;
        public String SharedString;
        public String Formula;
        public String StyleId;
        public String Comment;
        public String FontName;
        public String FontSize;
        public String FontColor;
        public String FontBold;
        public String Underline;
        public String Italic;
        public String AligmentHorizontal;
        public String AligmentVertical;
        public String FillType;
        public String FillForegroundColor;
        public String FillBackgroundColor;
        public String LeftBorder;
        public String RightBorder;
        public String TopBorder;
        public String BottomBorder;
        public String DiagonalBorder;
    }

    public static class MergeCellsList implements Serializable {
        public int row;
        public int col;
        public int rowspan;
        public int colspan;
    }

    public static class CommentCellsList implements Serializable {
        public int row;
        public int col;
        public String comment;
    }

    public static class CellFormatsList implements Serializable {
        public int count;
        public int numFmtId;
        public int borderId;
        public int fillId;
        public int fontId;
        public int applyAlignment;
        public int applyBorder;
        public int applyFont;
        public int applyNumberFormat;
        public int xfId;
        public String vertical;
        public String horizontal;
    }

    public static class BordersList implements Serializable {
        public String left;
        public String right;
        public String top;
        public String bottom;
        public String diagonal;
    }

    public static class FillsList implements Serializable {
        public String patternType;
        public String fgColor;
        public String bgColor;
    }

    public static class FontsList implements Serializable {
        public String fontsize;
        public String color;
        public String fontname;
        public String bold;
        public String italic;
        public String underline;
    }

    public static class NumFmtsList implements Serializable {
        public String formatCode;
        public int numFmtId;
    }

    public static class ExcelEntity implements Serializable {
        public String SheetName;
        public List<ApiEntity.SheetDataList> SheetData;
        public List<ApiEntity.CommentCellsList> Comments;
        public int TotalRow;
        public int TotalColumn;
        public List<ApiEntity.MergeCellsList> MergeCells;
        //		public List<ApiEntity.DataTable> LoadDataList;
    }

    public static class Express implements Serializable {
        public int Id;
        public String Name;
        public String Code;
        public String Url;
        public String Enumber;
        public String Checkcode;
        public int Method;
        public String Result;
        public String Enumname;
        public String Checkcodename;
        public String CheckcodeURL;
        public String RESULT_REG;
    }

    public static class FlowInfo implements Serializable {
        public int ID;
        public int StartRecordID;
        public int RecordID;
        public int UserID;
        public int FlowModuleID;
        public String Title;
        public String StartTime;
        public int State;
        public int NodeID;
        public int Parent;
    }

    public static class FlowHistory implements Serializable {
        public int ID;
        public int FlowID;
        public int RecordID;
        public int PageID;
        public int UserID;
        public String UserName;
        public String UserImage;
        public int AgentID;
        public String AgentName;
        public String LineName;
        public int LineID;
        public String Content;
        public int NodeID;
        public String NodeName;
        public String OperTime;
        public String NextUsers;
    }

    public static class FlowState implements Serializable {
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
        public int NextNode;
        public int PageID;
        //		public List<ApiEntity.DataStatus> DataStates;
    }

    public static class ApiType implements Serializable {
        public String Name;
        public String Path;
        public List<ApiEntity.ApiMethod> Methods;
    }

    public static class ApiMethod implements Serializable {
        public String Name;
        public String NameDesc;
        public String Path;
        public String HttpMethod;
        public String HttpMethodDesc;
        public List<ApiEntity.ApiParameter> ParaList;
    }

    public static class ApiParameter implements Serializable {
        public String Name;
        public String NameDesc;
        public String Type;
        public String TypeDesc;
        public String Desc;
    }

    public static class ApiDesc implements Serializable {
        public String Name;
        public String Desc;
        public List<ApiEntity.ApiParameter> ParaList;
        public String Return;
    }

    public static class Condition implements Serializable {
        public String Left;
        public String Right;
        public int Oper; //ConditionOpers
        public String Desc;
        public List<ApiEntity.Condition> Conditions;
    }

    public static class DataSource implements Serializable {
        public List<ApiEntity.UserTable> Tables;
        public ApiEntity.UserTable MainTable;
        public List<ApiEntity.TableRelation> Relations;
        public List<ApiEntity.Condition> Conditions;
        public String OrderBy;
    }

    public static class TableRelation implements Serializable {
        public String TargetTable;
        public int Type; //RelationType
        public List<ApiEntity.Condition> Conditions;
    }

    public static class GroupPower implements Serializable {
        public int GroupId;
        public int Power;
        public int Type; //GroupPowerTypes
        public int OID;
        public String PowerName;
    }

    public static class Language implements Serializable {
        public int ID;
        public String Name;
    }

    public static class PagePower implements Serializable {
        public int ID;
        public String Name;
        public String Memo;
        public int Language; //Languages
        public int TemplateID;
        public String Setting;
        public int PageID;
        public boolean IsChecked;
    }

    public static class Group implements Serializable {
        public int ID;
        public String Name;
        public String Memo;
        public int State;
        public int Parent;
        public int TemplateID;
    }

    public static class GroupEntity implements Serializable {
        public int ID;
        public String Name;
        public String Memo;
        public int TemplateID;
        public String TemplateName;
        public int State;
        public int Langague;
        public int Parent;
    }

    public static class PowerSetting implements Serializable {
        public int ID;
        public String Name;
        public String Memo;
        public int PID;
        public String SETTING;
    }

    public static class SysModule implements Serializable {
        public int ID;
        public String Name;
        public String URL;
    }

    public static class TemplateObject implements Serializable {
        public int ID;
        public String Name;
        public String Memo;
        public int TemplateID;
        public int Creator;
        //		public ApiEntity.Nullable`1,DateTime CreateTime;
    }

    public static class Template implements Serializable {
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
        public int Sort;
        public int Visible;
        public String AliasName;
        public List<ApiEntity.Navigation> NavList;
    }

    public static class Resource implements Serializable {
        public int TableID;
        public int Type; //ResourceTypes
        public String Content;
        public int Modifier;
        //		public ApiEntity.Nullable`1,DateTime ModifyTime;
        public int Language; //Languages
        public String Conditions;
        public int ID;
        public String Name;
        public String Memo;
        public int TemplateID;
        public int Creator;
        //		public ApiEntity.Nullable`1,DateTime CreateTime;
    }

    public static class UserFile implements Serializable {
        public int UserFileType; //UserFileTypes
        public int Language; //Languages
        public String Content;
        public int TableID;
        public boolean IsMain;
        public String CreateUserName;
        public int LastEditUser;
        public String LastEditUserName;
        public String LastEditTime;
        public int ID;
        public String Name;
        public String Memo;
        public int TemplateID;
        public int Creator;
        //		public ApiEntity.Nullable`1,DateTime CreateTime;
    }

    public static class UserTable implements Serializable {
        public String TableName;
        public int ID;
        public String Name;
        public String Image;
        public int Type; //UserTableTypes
        public int InfoID;
        public String Memo;
        public List<ApiEntity.UserField> Fields;
        public int Language; //Languages
        public String AliasName;
        public int SeqType; //SeqType
        public int Power; //UserPowers
        public boolean IsHasCreator;
        public boolean IsHasState;
        public boolean IsHasDesc;
        public boolean IsHasModifier;
        public boolean AllowEvent;
        public String NameField;
        public String TimeField;
        public String OperField;
        public boolean AllowTalker;
        public boolean IsSystem;
    }

    public static class UserView implements Serializable {
        public int TableID;
        public int ID;
    }

    public static class SystemInfo implements Serializable {
        public int ID;
        public String Name;
        public String Memo;
        public int Parent;
        public List<ApiEntity.UserTable> Tables;
        public int Language; //Languages
    }

    public static class UserField implements Serializable {
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

    public static class FieldValue implements Serializable {
        public String Text;
        public int Value;
        public boolean IsDefault;
        public boolean IsDisabled;
        public boolean IsDelete;
    }

    public static class RelationSetting implements Serializable {
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

    public static class TemplateBuyRecord implements Serializable {
        public int ID;
        public String CompanyCode;
        public String TemplateListID;
        public int NumForYear;
        public int UserCount;
        public String BuyTime;
        public int BuyState;
        public List<ApiEntity.Template> Templates;
        //		public ApiEntity.Double OrderMoney;
    }

    public static class Company implements Serializable {
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
        public int STORE;
        public String ICON;
    }

    public static class AreaInfo implements Serializable {
        public int ID;
        public String Code;
        public String Name;
        public String Memo;
        public int Type; //AreaType
        public int ParentID;
        public ApiEntity.AreaInfo Parent;
        public List<ApiEntity.AreaInfo> Children;
    }

    public static class WeekInfo implements Serializable {
        public String weekTime;
        public String weekText;
        public int state;//0本周内小于今天，1今天，2本周内今天之后
    }

    public static class PendingEvents implements Serializable {
        public List<ApiEntity.PendingEventInfo> weekOne;
        public List<ApiEntity.PendingEventInfo> weekTwo;
        public List<ApiEntity.PendingEventInfo> weekThree;
        public List<ApiEntity.PendingEventInfo> weekFour;
        public List<ApiEntity.PendingEventInfo> weekFive;
        public List<ApiEntity.PendingEventInfo> weekSix;
        public List<ApiEntity.PendingEventInfo> weekSeven;
    }

    public static class PendingEventInfo implements Serializable {
        public int ID;
        public int SendType;
        public int State;//1是未开始，2进行中，3延迟，4已完成
        public String Title;
        public String Mark;
    }
}