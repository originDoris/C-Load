package com.cl.framework.core.constants;

/**
 * message
 *
 * @author xhz
 */
public class MessageConstant {
   public static final  String PACKAGES = "DataTable system scanned {0}/{1} packages.";

   public static final String CLASSES = "DataTable system scanned {} classes in total.";

   public static final String SCAN_ENDPOINT = "( {} EndPoint ) The DataTable system has found {} components of @EndPoint.";

   public static final String SCAN_EVENTS = "( {1} Event ) The endpoint {0} scanned {1} events of Event, will be mounted to routing system.";

   public static final String SCAN_CONSUMER = "( {0} Consumer ) The DataTable system has found {0} components of @Consumer.";

   public static final String ADDRESS_IN = "Vert.x DataTable has found {0} incoming address from the system. Incoming address list as below: ";

   public static final String ADDRESS_ITEM = "       Addr : {0}";

   public static final String SCANNED_EVENTS = "( {1} Event ) The endpoint {0} scanned {1} events of Event, will be mounted to routing system.";
   public static final String SCANNED_INITIALS = "( {1} INITIALS ) The class {0} scanned {1} INITIAL , will be mounted to system.";
   public static final String SCANNED_DESTROYS = "( {1} DESTROYS ) The class {0} scanned {1} DESTROY , will be mounted to system.";

   public static final String SCAN_RECEIPTS = "( {1} Receipt ) The queue {0} scanned {1} records of Receipt, will be mounted to event bus.";

   public static final String AGENT_HIT = "( Agent ) The standard verticle {0} will be deployed.";

   public static final String VTC_OPT = "( Verticle ) The deployment options has been captured: instances = {0},  content = {1}";

   public static final String VTC_END = "( {3} ) The verticle {0} has been deployed {1} instances successfully. id = {2}.";

   public static final String VTC_FAIL = "( {3} ) The verticle {0} has been deployed {1} instances failed. id = {2}, cause = {3}.";

   public static final String VTC_STOPPED = "( {2} ) The verticle {0} has been undeployed successfully, id = {1}.";

   public static final String NULL_EVENT = "( {0} ) The system found \"null\" event in the consumer. ";

   public static final String SESSION_ID = "( Session ) \n\t\tPath = {0}, Session Id = {1}, Client Cookie Value {2}";

   public static final String HTTP_SERVERS = "( Http Server ) {0} (id = {1}) Agent has deployed HTTP Server on {2}.";

   public static final String MAPPED_ROUTE = "( Uri Register ) \"{1}\" has been deployed by {0}, Options = {2}.";

   public static final String HTTP_LISTEN = "( Http Server ) {0} Http Server has been started successfully. Endpoint: {1}.";

   public static final String AGENT_DEFINED = "User defined agent {0} of type = {1}, the default will be overwritten.";

   public static final String MSG_FUTURE = "( Invoker ) Invoker = {0}, ReturnType = {1}.";

   public static final String MSG_INVOKER = "( Invoker ) DataTable system selected {0} as invoker , the metadata receipt hash code = {1}, invoker size = {2}.";

   public static final String WORKER_HIT = "( Worker ) The worker verticl {0} will be deployed.";

   public static final String RESOLVER_CONFIG = "( Resolver ) Select resolver from annotation config \"{0}\" for Content-Type {1}";


   public static final String TOKEN_JWT = "Jwt token data stored: {0}.";

   public static final String TOKEN_STORE = "The system will singleton user's principle information. user key: {0}.";

   public static final String TOKEN_INPUT = "The system will verify token = {0}";

   public static final String SCANNED_JSR311 = "( Field ) JSR311 Warning, declared class: \"{0}\", field = \"{1}\", type = {2}";

   public static final String SCANNED_FIELD = "( Field ) Class \"{0}\" scanned field = \"{1}\" of {2} annotated with {3}. will be initialized with DI container.";

   public static final String SCANNED_INSTANCES = "The instance classes ({0}) will be scanned.";

   public static final String SCANNED_INJECTION = "( {1} Inject ) The DataTable system has found \"{0}\" object contains {1} components of @Inject or ( javax.inject.infix.* ).";

   public static final String PLUGIN_LOAD = "The raw data ( node = {0} ) has been detected plugin ( {1} = {2} )";

   public static final String INFIX_IMPL = "The hitted class {0} does not implement the interface of {1}";

   public static final String INFIX_NULL = "The system scanned null infix for key = {0} on the field \"{1}\" of {2}";

   public static final String JOOQ_FIELD = "( Pojo ) The field \"{0}\" has been hitted ( converted ) to \"{1}\"";

   public static final String Q_ALL = "Linear keys = \"{0}\", Tree keys = \"{1}\" in current level scanning.";

   public static final String Q_STR = "Connect operator selected: \"{0}\" when parsing query tree.";

   public static final String JOOQ_PARSE = "( Jooq -> Condition ) Parsed result is \ncondition = \n{0}.";

   public static final String JOOQ_TERM = "`com.cl.framework.plugin.jooq.util.condition.Term` selected: `{0}` by op = `{1}`.";
   public static final String JOOQ_TERM_ERR = "`com.cl.framework.plugin.jooq.util.condition.Term` is null when op = `{0}`.";

   public static final String JOB_NO_OFF = "[ Job ] Current job `{0}` does not has @Off method.";

   public static final String SCANED_JOB = "( {0} Job ) The DataTable system has found {0} components of @Job.";

   public static final String JOB_IGNORE = "[ Job ] The class {0} annotated with @Job will be ignored because there is no @On method defined.";

   public static final String JOB_CONFIG = "[ Job ] Job configuration read : {0}";

   public static final String IS_STOPPED = "( Job ) The timeId {0} does not exist in RUNNING pool of jobs.";

   public static final String IS_RUNNING = "( Job ) The job {0} has already been running !!!";

   public static final String IS_STARTING = "( Job ) The job {0} is booting, please preparing for READY";

   public static final String IS_ERROR = "( Job ) The job {0} met error last time, please contact administrator and try to resume.";

   public static final String NOT_RUNNING = "( Job ) The job {0} is not running, the status is = {1}";

   public static final String JOB_SCANNED = "[ Job ] The system scanned {0} jobs with type {1}";

   public static final String JOB_COMPONENT_SELECTED = "[ Job ] {0} selected: {1}";

   public static final String JOB_EMPTY = "DataTable system detect no jobs, the scheduler will be stopped.";

   public static final String JOB_MONITOR = "DataTable system detect {0} jobs, the scheduler will begin....";

   public static final String JOB_CONFIG_NULL = "( Ignore ) Because there is no definition in `vertx.yml`, Job container is stop....";

   public static final String JOB_AGHA_SELECTED = "[ Job: {1} ] Agha = {0} has been selected for job {1} of type {2}";

   public static final String PHASE_1ST_JOB = "[ Job: {0} ] 1. Input new data of JsonObject";

   public static final String PHASE_1ST_JOB_ASYNC = "[ Job: {0} ] 1. Input from address {1}";

   public static final String PHASE_2ND_JOB = "[ Job: {0} ] 2. Input without `JobIncome`";

   public static final String PHASE_2ND_JOB_ASYNC = "[ Job: {0} ] 2. Input with `JobIncome` = {1}";

   public static final String PHASE_3RD_JOB_RUN = "[ Job: {0} ] 3. --> @On Method call {1}";

   public static final String PHASE_6TH_JOB_CALLBACK = "[ Job: {0} ] 6. --> @Off Method call {1}";

   public static final String PHASE_4TH_JOB_ASYNC = "[ Job: {0} ] 4. Output with `JobOutcome` = {1}";

   public static final String PHASE_4TH_JOB = "[ Job: {0} ] 4. Output without `JobOutcome`";

   public static final String PHASE_5TH_JOB = "[ Job: {0} ] 5. Output directly, ignore next EventBus steps";

   public static final String PHASE_5TH_JOB_ASYNC = "[ Job: {0} ] 5. Output send to address {1}";

   public static final String PHASE_ERROR = "[ Job: {0} ] Terminal with error: {1}";

   public static final String JOB_DELAY = "[ Job: {0} ] Job will started after {1} ms";

   public static final String JOB_ADDRESS_EVENT_BUS = "[ Job ] {0} event bus enabled: {1}";

   public static final String JOB_POOL_START = "[ Job ] `{0}` worker executor will be created. The max executing time is {1} s";

   public static final String JOB_POOL_END = "[ Job ] `{0}` worker executor has been closed! ";

   public static final String JOB_MOVED = "[ Job ] Type = {0}, `{1}` The status has been moved: {2} -> {3}";

   public static final String JOB_TERMINAL = "[ Job ] {0} The job will be terminal, status -> ERROR";

   public static final String JOB_INTERVAL = "[ Job ] `{0}` The scheduler will start after {1} ms, then scheduled duration {2} (-1 means ONCE) ms in each, timerId = {3}";

}
