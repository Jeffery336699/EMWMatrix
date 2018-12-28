package cc.emw.mobile.net;

public class ApiEnum {
	public class GridType{
		public static final int Normal = 0;
		public static final int Tree = 1;
		public static final int Group = 2;
	}
	public class CompareTypes{
		public static final int Equal = 1;
		public static final int NotEqual = 2;
		public static final int Greater = 3;
		public static final int Smaller = 4;
		public static final int GreaterEqual = 5;
		public static final int SmallerEqual = 6;
		public static final int IsNull = 7;
		public static final int NotNull = 8;
		public static final int Contain = 9;
		public static final int NotContain = 10;
		public static final int StartsWith = 11;
		public static final int NotStartsWith = 12;
		public static final int In = 13;
		public static final int NotIn = 14;
		public static final int LatelyDay = 15;
		public static final int LatelyMonth = 16;
		public static final int Between = 17;
	}
	public class ExportType{
		public static final int Current = 1;
		public static final int Selected = 2;
		public static final int RowCount = 3;
		public static final int All = 4;
	}
	public class RoleTypes{
		public static final int User = 0;
		public static final int Group = 1;
		public static final int DEPT = 2;
	}
	public class UserTypes{
		public static final int UnKnown = 0;
		public static final int Developer = 1;
		public static final int SystemUser = 2;
		public static final int WebSiteUser = 3;
	}
	public class ChatType{
		public static final int UnKown = 0;
		public static final int User = 1;
		public static final int Group = 2;
		public static final int System = 3;
	}
	public class MessageType{
		public static final int LoginIn = 1;
		public static final int LoginOut = 2;
		public static final int ServerToken = 3;
		public static final int Message = 4;
		public static final int Flow = 5;
		public static final int Attach = 6;
		public static final int Image = 7;
		public static final int Audio = 8;
		public static final int Video = 9;
		public static final int Share = 10;
		public static final int Task = 11;
	}
	public class UserNoteSendTypes{
		public static final int Public = 0;
		public static final int Private = 1;
	}
	public class UserNoteAddTypes{
		public static final int Normal = 0;
		public static final int Notice = 1;
		public static final int Image = 2;
		public static final int File = 3;
		public static final int Link = 4;
		public static final int Vote = 5;
		public static final int Record = 6;
		public static final int Schedule = 7;
		public static final int Task = 8;
		public static final int Plan = 9;
		public static final int Share = 10;
	}
	public class ScheduleTags{
		public static final int Meet = 0;
		public static final int Visit = 1;
		public static final int Tel = 2;
		public static final int Email = 3;
		public static final int Report = 4;
		public static final int Other = 5;
		public static final int Custom = 6;
	}
	public class UserFenPaiFlowState{
		public static final int Normal = 1;
		public static final int Submit = 2;
		public static final int Rework = 3;
	}
	public class NoteRoleTypes{
		public static final int Public = 0;
		public static final int User = 1;
		public static final int Group = 2;
		public static final int Dept = 3;
		public static final int PowerRole = 4;
	}
	public class ChartValueCountType{
		public static final int CountAll = 0;
		public static final int CountNotNull = 1;
		public static final int Sum = 2;
		public static final int Avg = 3;
		public static final int Max = 4;
		public static final int Min = 5;
	}
	public class NavigationTypes{
		public static final int None = 0;
		public static final int Grid = 1;
		public static final int Form = 2;
		public static final int ModuleFun = 3;
	}
	public class ChatterGroupTypes{
		public static final int Public = 0;
		public static final int Private = 1;
	}
	public class UserFilePowerType{
		public static final int None = 0;
		public static final int View = 1;
		public static final int Cooperation = 2;
		public static final int Owner = 3;
	}
	public class States{
		public static final int Normal = 0;
		public static final int Disable = 1;
	}
	public class ConditionOpers{
		public static final int Equal = 1;
		public static final int NotEquals = 2;
		public static final int GreaterThan = 3;
		public static final int LessThan = 4;
		public static final int GreaterThanOrEqual = 5;
		public static final int LessThanOrEqual = 6;
		public static final int IsNull = 7;
		public static final int IsNotNull = 8;
		public static final int Like = 9;
		public static final int NoLike = 10;
		public static final int StartWith = 11;
		public static final int NotStartWidth = 12;
		public static final int In = 13;
		public static final int NotIn = 14;
		public static final int NearDay = 15;
		public static final int NearMonth = 16;
		public static final int BeforeDay = 17;
		public static final int BeforeMonth = 18;
		public static final int Other = 30;
		public static final int Not = 100;
		public static final int And = 101;
		public static final int Or = 102;
	}
	public class ConditionTypes{
		public static final int Tool = 0;
		public static final int Execute_Main = 1;
		public static final int Execute_Sub = 2;
		public static final int DataSource = 3;
		public static final int Seacher = 4;
		public static final int Message = 5;
		public static final int DataPower = 6;
		public static final int Trigger = 7;
		public static final int Task = 8;
	}
	public class RelationType{
		public static final int Left = 0;
		public static final int Right = 1;
		public static final int Inner = 2;
		public static final int Outer = 3;
		public static final int Cross = 4;
	}
	public class GroupPowerTypes{
		public static final int Data = 1;
		public static final int Page = 2;
		public static final int Navigation = 3;
		public static final int Portal = 4;
	}
	public class Languages{
		public static final int ZHCN = 0;
		public static final int EN = 1;
		public static final int ZHTW = 2;
	}
	public class ResourceTypes{
		public static final int SearchSet = 1;
		public static final int Condition = 2;
		public static final int AutoCode = 3;
		public static final int SearchString = 4;
		public static final int Insert = 5;
		public static final int Update = 6;
		public static final int Delete = 7;
		public static final int CreateFile = 8;
		public static final int Script = 9;
		public static final int Email = 10;
		public static final int SystemMessage = 11;
		public static final int Message = 12;
	}
	public class ToolTypes{
	}
	public class UserFileTypes{
		public static final int Grid = 0;
		public static final int Form = 1;
		public static final int Report = 2;
		public static final int Folder = 3;
		public static final int Flow = 4;
		public static final int Navigation = 5;
		public static final int MailTemplate = 6;
		public static final int ModelForm = 7;
		public static final int Template = 8;
		public static final int Power = 9;
		public static final int CustomPage = 10;
	}
	public class SeqType{
		public static final int None = 0;
		public static final int Private = 1;
		public static final int Public = 2;
	}
	public class UserPowers{
		public static final int Normal = 0;
		public static final int ReadOnly = 1;
		public static final int Hidden = 2;
	}
	public class UserTableTypes{
		public static final int Table = 1;
		public static final int View = 2;
		public static final int DataSource = 3;
	}
	public class InputFormats{
		public static final int None = 0;
		public static final int Email = 1;
		public static final int Url = 2;
		public static final int Phone = 3;
	}
	public class DBType{
		public static final int None = 0;
		public static final int String = 1;
		public static final int Int = 2;
		public static final int Double = 3;
		public static final int Date = 4;
		public static final int Money = 5;
		public static final int Text = 6;
		public static final int Boolean = 7;
	}
	public class InputTypes{
		public static final int None = 0;
		public static final int Input = 1;
		public static final int Select = 2;
		public static final int AutoCode = 3;
		public static final int Search = 4;
		public static final int MultiSelect = 5;
	}
	public class AreaType{
		public static final int Continent = 1;
		public static final int ContinentArea = 2;
		public static final int Country = 3;
		public static final int CountryArea = 4;
		public static final int Province = 5;
		public static final int City = 6;
		public static final int What = 7;
		public static final int County = 8;
	}
	public class RSTATE{
		public static final int Activated = 1;
		public static final int Inactive = -1;
	}
}