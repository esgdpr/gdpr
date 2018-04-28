package ee.ut.gdpr.proto.domain.enums;

public enum ValidationError {
    CONSENT_MISSING(
            "Consent class is missing.",
            "Consent for this processing activity was not collected from data subject.",
            "Data subject should give consent for each purpose of processing his/her personal data. prior personal data processing.\n" +
                    "Consent should be collected in one of the tasks that are followed by processing activity. Some tips:\n" +
                    "1. If you have defined consent task in one of your business process files then check whether it has " +
                    "attached property with name 'consent'.\n" +
                    "2. Tool searches consent tasks in previous processes or previous tasks. That means " +
                    "if consent task is in process with order 5, it will be ignored when analysing process with " +
                    "order 4."),
    CONSENT_POORLY_COLLECTED(
            "Data Subjects Consent for Processing his/hers\\ndata was poorly collected.",
            "Data Subjects Consent for Processing his/hers data was poorly collected.",
            "At least one of the class boolean attributes is false. [Art. 4(11), 7 GDPR]"),
    LOGGING_MISSING(
            "Record(Logging) class is missing.",
            "The processing is not recorded/audited",
            "Each processing activity should be followed by 'recording' activity, that would " +
                    "log processing of personal data. [Art. 30 GDPR]"),
    PURPOSE_MISSING(
            "Purpose class is missing.",
            "Purpose class is missing.",
            "Purpose for this processing activity is missing without purpose consent cannot be given by data subject, " +
                    "moreover processing is prohibited with-out clear purpose Purpose of processing can be defined in " +
                    "'Step 1' of the analysis. [Art. 4(2), 6(1a) GDPR]"),
    RECTIFICATION_PROCESS_MISSING(
            "Process to rectify personal data is missing.",
            "Process to rectify personal data is missing.",
            "Provide process (upload .bpmn file and choose appropriate type of process) " +
                    "that would allow data subject to rectify inaccurate personal data concerning him/her [Art. 16 GDPR]"),
    ERASURE_PROCESS_MISSING(
            "Process to erase personal data is missing.",
            "Process to erase personal data is missing (right to be forgotten)",
            "Provide process (upload .bpmn file and choose appropriate type of process) " +
                    "that would allow data subject to erase personal data concerning him/her [Art. 17 GDPR]"),
    RESRICTION_PROCESS_MISSING(
            "Process to restrict personal data processing\\nis missing.",
            "Process to restrict personal data processing is missing.",
            "Provide process (upload .bpmn file and choose appropriate type of process) " +
                    "that would allow data subject to restrict personal data processing done by this activity [Art. 18 GDPR]"),
    PORTABILITY_PROCESS_MISSING(
            "Process to export personal data is missing.",
            "Process to export personal data is missing.",
            "Provide process (upload .bpmn file and choose appropriate type of process) " +
                    "that would allow data subject to access and export personal data being processed/collected by " +
                    "this activity [Art. 20 GDPR]"),
    NOTIFICATION_PROCESS_MISSING(
            "Process to notify data subject about disclosure/\\nrectification/erasure/restriction is missing.",
            "Process to notify data subject about disclosure/rectification/erasure/restriction is missing.",
            "Provide process (upload .bpmn file and choose appropriate type of process) " +
                    "that would allow data subject to be notified about each rectification, erasure, " +
                    "restriction of personal data processing or disclosure of his/hers personal data to " +
                    "third parties or recipients [Art. 19 GDPR]"),
    ACCESS_PROCESS_MISSING(
            "Process for data subject to access information\\nabout personal data processing is missing.",
            "Process for data subject to access information about personal data processing is missing.",
            "Provide process (upload .bpmn file and choose appropriate type of process) " +
                    "that would allow data subject to access information about processing his/hers data, " +
                    "including such data as: purpose, categories of personal data, recipients etc. [Art. 15 GDPR]"),
    CRIMINAL_ERROR(
            "Processor is not authorized to process this data.",
            "This activity is processing personal data relating to criminal convictions and offences, however the " +
                    "authorization to such activity was not granted by official authority. ",
            "In order to legally process such data, processor should be authorized by official authority " +
                    "(controller) [Art. 10 GDPR]"),
    CAREFUL_PROCESSING("",
            "Processing of personal data with special category should undertake additional measures. (e.g. explicit consent given by data subject)",
            "This activity is processing special category of personal data and therefore should be carefully handled. " +
                    "Most likely processing of such data is prohibited, however there are some exclusions such as " +
                    "explicit consent from data subject, please see [Art. 9 GDPR]"),
    CROSS_BORDER("",
            "Cross-border processing",
            "This activity is dealing with cross-border processing of personal data. If this activity is processing " +
                    "personal data of EU citizens, then GDPR will still apply, even outside of the EU. [Art. 3 GDPR]");

    String modelError;
    String finalError;
    String suggestion;

    ValidationError(String modelError, String finalError, String suggestion) {
        this.modelError = modelError;
        this.finalError = finalError;
        this.suggestion = suggestion;
    }

    public String getModelError() {
        return modelError;
    }

    public String getFinalError() {
        return finalError;
    }

    public String getSuggestion() {
        return suggestion;
    }
}
