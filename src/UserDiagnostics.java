import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Created by jsuter on 7/14/17.
 * Modified by Justin on 11/6/2017. --ported to project
 */

public class UserDiagnostics {

    private static UserDiagnostics instance = null;
    protected static Logger logger = null;
    protected UserDiagnostics() {
        // Exists only to defeat instantiation.
    }
    public static UserDiagnostics getInstance() {
        if(instance == null) {
            instance = new UserDiagnostics();
        }

        if (logger == null) {
            logger = LoggerFactory.getLogger("LE");
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            StatusPrinter.print(lc);
            // This is a problem. TODO -- exactly where to initialize this? Not sure how to get the application context CAS
        }

        return instance;
    }

    public static void initLog()
    {
        if(instance == null) {
            instance = new UserDiagnostics();
        }

        if (logger == null) {
                logger = LoggerFactory.getLogger("LE");
        }
    }

    public static void logActivity(String em, String lm) {

        if (logger != null)
        {
            // Emoticon and log message
            String userid = "no user";
            logger.info("em:\'" + em + "\' message=" + lm + " action:\'" + UserDiagnostics.emojitoWord(em) + "' userid:\'" + userid.toUpperCase() +"\'");
        }else{
            System.out.println("Logger is null");
        }


    }


    public static final class Constants {

        public static final String MEMORY_WARNING = "🐷";

        public static final String DEVICE = "📱";

        public static final String FORCE_CRASH = "💔";

        public static final String APPLICATION_START = "🐤";

        public static final String MEMORY_USED = "💽";

        public static final String BUILD_INFO= "🌱";

        public static final String LEAD_OPENED = "📘";

        public static final String ASYNCHRONOUS = "🎱";

        public static final String CLEAR_CACHE = "👻";

        public static final String UNSAVED_DOCUMENT_CRASH = "🎯";

        public static final String DOCUMENT_OPENED = "📕";

        public static final String INTERESTING_EVENT = "🌾";

        public static final String SUCCESSFULLY_PASSED_ERROR_CONDITION = "🤑";

        public static final String FAILED_LOGIN = "😡";

        public static final String CHANGE_ORDER_PAYMENT = "🍊";

        public static final String LEAD_PAYMENT = "🍎";

        public static final String FINANCIAL_BREADCRUMB = "🏦";

        public static final String MISCELLANEOUS_BAD_MESSAGE = "👹";

        public static final String ERROR_CONDITION_WE_SHOULD_NOT_HIT = "👺";

        public static final String PAGE_COUNT_FOR_DOCUMENT = "✳️";

        public static final String SERVER_ERROR = "⁉️";

        public static final String PAYMENT_ERROR = "🥀";

        public static final String PAYMENT_OF_SOME_SORT = "🐬";

        public static final String CALL_EXTERNAL_WEB_VIEW = "💼";

        public static final String APPLICATION_TERMINATE = "🌔";

        public static final String ORDER_LOCKED = "❄️";

        public static final String GENERATE_TEMPLATE_FOR_ORDER = "🔩";

        public static final String GENERATE_TEMPLATE_FOR_LEAD = "🛠";

        public static final String GENERATE_TEMPLATE_FOR_CHANGE_ORDER = "⛏";

        public static final String UPDATE_ORDER_WITH_A_NOTE = "✏️";

        public static final String CANCEL_CHANGE_ORDER = "🚽";

        public static final String CHANGE_ORDER_DETAILS = "👞";

        public static final String DOCUMENT_SEND_EMAIL = "📫";

        public static final String LEAD_CREATE = "🎁";

        public static final String EPA_RELATED = "♻️";
    }

    public static String emojitoWord (String emoticonKey )
    {
        switch (emoticonKey)
        {
            case "🐷": return "MEMORY_WARNING";
            case "🐤": return "APPLICATION_START";
            case "📱": return "DEVICE";
            case "💔": return "FORCE_CRASH";
            case "💽": return "MEMORY_USED";
            case "🌱": return "BUILD_INFO";
            case "📘": return "LEAD_OPENED";
            case "🎱": return "ASYNCHRONOUS";
            case "👻": return "CLEAR_CACHE";
            case "🎯": return "UNSAVED_DOCUMENT_CRASH";
            case "📕": return "DOCUMENT_OPENED";
            case "🌾": return "INTERESTING_EVENT";
            case "🤑": return "SUCCESSFULLY_PASSED_ERROR_CONDITION";
            case "😡": return "FAILED_LOGIN";
            case "🍊": return "CHANGE_ORDER_PAYMENT";
            case "🍎": return "LEAD_PAYMENT";
            case "🏦": return "FINANCIAL_BREADCRUMB";
            case "👹": return "MISCELLANEOUS_BAD_MESSAGE";
            case "👺": return "ERROR_CONDITION_WE_SHOULD_NOT_HIT";
            case "⁉️": return "SERVER_ERROR";
            case "🥀": return "PAYMENT_ERROR";
            case "🐬": return "PAYMENT_OF_SOME_SORT";
            case "💼": return "CALL_EXTERNAL_WEB_VIEW";
            case "🌔": return "APPLICATION_TERMINATE";
            case "❄️": return "ORDER_LOCKED";
            case "🔩": return "GENERATE_TEMPLATE_FOR_ORDER";
            case "🛠": return "GENERATE_TEMPLATE_FOR_LEAD";
            case "⛏": return "GENERATE_TEMPLATE_FOR_CHANGE_ORDER";
            case "✏️": return "UPDATE_ORDER_WITH_A_NOTE";
            case "🚽": return "CANCEL_CHANGE_ORDER";
            case "👞": return "CHANGE_ORDER_DETAILS";
            case "📫": return "DOCUMENT_SEND_EMAIL";
            case "🎁": return "LEAD_CREATE";
            default: return "NONE";
        }

    }

/* Example messages

 e:'🐷' message='Did receive memory warning in Base VC'  className:'SelectDocumentTypeViewController' func:'didReceiveMemoryWarning' support='iPad6,11' userid:GXF9706
 e:'🐤' message='Install2Go Launched'  className:'AppDelegate' func:'application:didFinishLaunchingWithOptions:' support='👎' userid:NoServiceUser Context
 Context
 e:'🌱' message='Login success' appVersion='2.16' appBuild='11' userid:'DXC9817  ' model:'iPad' platform:'iPad5,4' systemVersion:'10.2'  systemName:'iOS' deviceName:'DannysiPad'  physicalMemory:1988 humanModel='iPad Air 2' support='🤜' Context
 e:'📘' message='Load lead as document'  identifier:'1-10910678957' program:'SF&I Pergola'  className:'LeadViewController' func:'initAsDocumentsWithLead:' support='🤜' userid:DXC9817 Context
 e:'🎯' message='Crashed document'  recoverPDF:'[DRAFT] HS-275 Water Treatment - Agreement' identifierType:'LEAD_NBR' identifier:'1-10929837718'  className:'AutoSaveDataHelper' func:'hasUnsavedData()' support='🤜' userid:JXP9863 Context
 e:'📕' message='Load document'  form:'HS-225 In Home Decorating - Agreement' lead:'1-10909269117' program:'SF&I Interior Shutters' state:'CA'  className:'SelectDocumentTypeViewController' func:'collectionView:didSelectItemAtIndexPath:' support='iPad6,11' userid:GXF9706 Context
 e:'🤑' message='No document crash last session' autosaveDetectUnsavedData=N  className:'AutoSaveDisplayAlertHelper.Type' func:'displayAlertIfNecessary()' support='👎' userid:BXW9025 Context
 e:'😡' message='Login failed for user' failedUserId='JXK9146'  className:'LoginViewController' func:'handleLogin:' support='👍' userid:NoServiceUser Context
 e:'🍊' message='Begin change order'  identifier:'13442957' storeNumber:'2213' order:'228140'  className:'MultistepViewController' func:'changeOrderFlowControllerWithChangeOrderNode:orderDetails:' support='🤜' userid:MXC9495 Context
 🍎
 e:'🏦' message=Authorization processed successfully  className:'PaymentNode' func:'logCrittercismMessage:' support='👍' userid:AYC9658 Context
 👹
 e:'👺' message='Document found in cache when one is not expected'  className:'PDFDisplayController' func:'createFileInDocuments:' support='🤜' userid:AXH9009 Context
 e:'✳️' message='PageCount' pagecount=14 form:'DOCUMENT.pdf' lead:1-10910678957 program:SF&I Pergola  className:'PDFViewControllerHD' func:'_loadPDFDoc:password:isInvalidPassword:' support='🤜' userid:DXC9817 Context
 e:'⁉️' message='Server error'  errorMessage='Routing number and routing number confirmation do not match.'  className:'CheckInfoViewController' func:'i_handleNext:' support='🤜' userid:FXZ9964 Context
 e:'🥀' message=Transaction Declined by card processor. (Error 0005)  className:'WebService' func:'errorMessageForResponse:' support='🤜' userid:KXD9332 Context
 e:'🐬' message='Send payment' paymentMethod='Credit' paymentAmount='952' paymentType ='leadPayment'  className:'PaymentNode' func:'sendPaymentLog:' support='👍' userid:DXJ9744 Context
 e:'💼' message='Message center'  url:'https://hdconnect.homedepot.com/Pages/MessageCenterView.aspx'   className:'DashboardViewController' func:'i_handleMessages:' support='🤜' userid:RYD9864 Context
 e:'🌔' message='Application will terminate'  className:'AppDelegate' func:'applicationWillTerminate:' support='👍' userid:NoServiceUser Context
 e:'❄️' message='Order locked'  errorMessage:'Change Order is currently locked. Please contact store for assistance. ****SERVICE_ID = 27ee2936-a987-49c1-95ef-56c7f4b826a1'   className:'ChangeOrderNode' func:'perform' support='🤜' userid:MXC9495 Context
 e:'🔩' message='Generate template for PO'  form:'HS-813 Problem Sheet' order:'01403361' program:'59-55' state:'CA'  className:'SelectDocumentTypeViewController' func:'generateTemplate' support='🤜' userid:RYD9864 Context
 e:'⛏' message='Generate template for PO'  form:'COForm' order:'04439705' program:'program unknown' state:'MA'  className:'ChangeOrderNode' func:'generateCODocument' support='🤜' userid:RXA9584 Context
 e:'✏️' message='Update order with a note'  identifier:'257978' storeNumber:'4915'   className:'CreatePONoteAlert' func:'i_saveButtonTapped:' support='🤜' userid:EXD9656 Context
 e:'🚽' message='Cancel change order' autosaveDetectUnsavedData=N  className:'CancelOrderRequest.Type' func:'cancelRequestFromOrderDetails' support='🤜' userid:MXC9495 Context
 👞
 e:'📫' message='Apigee request JSON'  errorMessage='{"messageSourceCode":"1003","eccMessageHeader":{"message":"Your Requested Installation Documents","fromAddress":"noreply@homedepot.com","messageType":"EMAIL"},"docDeleteFlag":"Y","emailDelivery":{"recipients":{"recipient":[{"destination":"TO","emailaddress":"homer.test@gmail.com"}]},"subject":"Your Requested Installation Documents","attachments":{"attachment":[{"do'  className:'HomeDepot.ECCMessageRequest' func:'encodedData' support='✊' userid:RXT9221 QA 2.17 iOS Logging/iOS Log Context
 🎁


 */
}