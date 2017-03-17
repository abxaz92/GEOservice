package ru.macrobit.geoservice.common;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.query.Query;
import ru.macrobit.geoservice.dao.MainPropertiesDAO;
import ru.macrobit.geoservice.pojo.MainProperties;
import ru.macrobit.geoservice.search.pojo.Weights;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import java.util.*;

@Startup
@Singleton
@Lock(LockType.READ)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@DependsOn("SecondMongotemplateProducer")
public class TaxiProperties {
    public static final Logger LOG = Logger.getLogger(TaxiProperties.class);
    public static final boolean persistTimer = false;

    private String node;
    private String certPath;
    private String certPass;
    private String atsIp;
    private String atsPort;
    private String googleMapsKey;
    private String smsCentrUser;
    private String smsCentrPass;
    private List<String> adminEmails;

    private String nominatimHost;

    private String bankKeySecret;
    private String bankKeyPublic;
    private String bankServiceId;

    private String pushClientAPIkey; // =
    // "AIzaSyDU-OCpT-GT0kktxn_4X6Kz_XvBjZuz-Yw";
    private String pushClientProjectId; // = "t-monument-671";
    private String pushDriverAPIkey; // =
    // "AIzaSyDU-OCpT-GT0kktxn_4X6Kz_XvBjZuz-Yw";
    private String pushDriverProjectId; // = "t-monument-671";

    private double clientAppDefaultCost;
    private int publicNearCarsLimit;

    private int maxBonusCompPercent;
    private int maxBonusCompSum;

    private Weights searchWeights;

    private List<String> chatTemplates;

    private String dropboxPath;
    private long socketMaxIdleTimeout;
    private boolean closeOnDouble;
    private double qiwiWalletComission = 0.02;
    private double qiwiTerminalComission;
    private long chatMessageTTL;
    private String clientOrderImpossibleString;
    private boolean checkDriversAvalibleForClients;
    private long callersQueueCountFetchInterval;

    private boolean enableAreaExistCriteria;
    private boolean useInmemoryAddressSearch;
    private String appLink;
    private boolean useSMSTranslitMsgs;
    private boolean useOrderCacheForDisps;
    private boolean excludeGeoObjectsFromSearch;
    private String routeServiceAddres = "http://80.87.201.17:8080";
    private int routeDistanceFetchBatchSize = 20;
    private int routeDistanceResponseWaitTimeout = 25;
    private boolean enableSecureMode;
    private boolean userGeoserviceForSuggestDistances;
    private Set<Double> extraCosts = new HashSet<>();
    private Set<String> cancelCauseTypes = new HashSet<>();
    private long locationUpdateExpireTimeout = 60000;
    private double locationUpdateSpeed = 17;
    private String imagesFolder = "/www/data/images/";
    private List<Integer> driverTimeSuggestions = new ArrayList<>(Arrays.asList(2, 4, 6, 8, 10, 12, 15, 20, 30));

    @Inject
    private MainPropertiesDAO propertiesDAO;

    @PostConstruct
    @Lock(LockType.WRITE)
    public void reload() {
        MainProperties properties = propertiesDAO.findOne(new Query(), null);
        if (properties != null) {
            this.node = properties.getNode();
            this.certPath = properties.getCertPath();
            this.certPass = properties.getCertPass();
            this.atsIp = properties.getAtsIp();
            this.atsPort = properties.getAtsPort();

            this.atsIp = properties.getAtsIp();
            this.atsPort = properties.getAtsPort();

            this.googleMapsKey = properties.getGoogleMapsKey();
            this.smsCentrUser = properties.getSmsCentrUser();
            this.smsCentrPass = properties.getSmsCentrPass();
            this.adminEmails = properties.getAdminEmails();

            this.nominatimHost = properties.getNominatimHost();

            this.bankKeySecret = properties.getBankKeySecret();
            this.bankKeyPublic = properties.getBankKeyPublic();
            this.bankServiceId = properties.getBankServiceId();
            this.pushClientAPIkey = properties.getPushClientAPIkey();
            this.pushDriverAPIkey = properties.getPushDriverAPIkey();
            this.pushClientProjectId = properties.getPushClientProjectId();
            this.pushDriverProjectId = properties.getPushDriverProjectId();
            this.clientAppDefaultCost = properties.getClientAppDefaultCost();
            this.publicNearCarsLimit = properties.getPublicNearCarsLimit();

            this.maxBonusCompPercent = properties.getMaxBonusCompPercent();
            this.maxBonusCompSum = properties.getMaxBonusCompSum();

            this.searchWeights = properties.getSearchWeights();

            this.chatTemplates = properties.getChatTemplates();
            this.dropboxPath = properties.getDropboxPath();
            this.socketMaxIdleTimeout = properties.getSocketMaxIdleTimeout();
            this.closeOnDouble = properties.isCloseOnDouble();
            this.qiwiTerminalComission = properties.getQiwiTerminalComission();
            this.qiwiWalletComission = properties.getQiwiWalletComission();
            this.clientOrderImpossibleString = properties.getClientOrderImpossibleString();
            this.checkDriversAvalibleForClients = properties.isCheckDriversAvalibleForClients();
            this.chatMessageTTL = properties.getChatMessageTTL();
            this.callersQueueCountFetchInterval = properties.getCallersQueueCountFetchInterval();
            this.enableAreaExistCriteria = properties.isEnableAreaExistCriteria();
            this.useInmemoryAddressSearch = properties.isUseInmemoryAddressSearch();
            this.appLink = properties.getAppLink();
            this.useSMSTranslitMsgs = properties.isUseSMSTranslitMsgs();
            this.useOrderCacheForDisps = properties.isUseOrderCacheForDisps();
            this.excludeGeoObjectsFromSearch = properties.isExcludeGeoObjectsFromSearch();
            this.routeServiceAddres = properties.getRouteServiceAddres();
            this.routeDistanceFetchBatchSize = properties.getRouteDistanceFetchBatchSize();
            this.routeDistanceResponseWaitTimeout = properties.getRouteDistanceResponseWaitTimeout();
            this.enableSecureMode = properties.isEnableSecureMode();
            this.userGeoserviceForSuggestDistances = properties.isUserGeoserviceForSuggestDistances();
            this.extraCosts = properties.getExtraCosts();
            this.cancelCauseTypes = properties.getCancelCauseTypes();
            this.locationUpdateExpireTimeout = properties.getLocationUpdateExpireTimeout();
            this.locationUpdateSpeed = properties.getLocationUpdateSpeed();
            this.imagesFolder = properties.getImagesFolder();
            this.driverTimeSuggestions = properties.getDriverTimeSuggestions();
        } else {
            setDefaults();
        }

    }

    private void setDefaults() {
        this.node = "main";
        this.certPath = "/var/lib/tomcat7/webapps/cert.p12";
        this.certPass = "1593";
        this.atsIp = "212.109.220.73";
        this.atsPort = "8088";

        this.googleMapsKey = "AIzaSyDU-OCpT-GT0kktxn_4X6Kz_XvBjZuz-Yw";
        this.smsCentrUser = "asmai@mail.ru";
        this.smsCentrPass = "Q4862513";
        this.adminEmails = Arrays.asList("chaava01@gmail.com");

        this.nominatimHost = "http://62.109.0.195/nominatim/search.php/";

        this.bankKeySecret = "a19a54588b7cf4cb0faa780725e3b76c";
        this.bankKeyPublic = "wG0R07b2yc0ZLPrT7HijvZJc5TYrkBfcmOMH4Da2C8g";
        this.bankServiceId = "61971";

        this.pushClientAPIkey = "AIzaSyDU-OCpT-GT0kktxn_4X6Kz_XvBjZuz-Yw";
        this.pushClientProjectId = "t-monument-671";
        this.pushDriverAPIkey = "AIzaSyDVC6anSV3HMJdFUfE8Sbd2xYrJ3qZPH40";
        this.pushDriverProjectId = "steam-outlet-714";
        this.clientAppDefaultCost = 90;
        this.publicNearCarsLimit = 50;

        this.maxBonusCompPercent = 30;
        this.maxBonusCompSum = 50;

        this.chatTemplates = new ArrayList<>();

        this.searchWeights = new Weights();
    }

    public String getNode() {
        return node;
    }

    public String getCertPath() {
        return certPath;
    }

    public String getCertPass() {
        return certPass;
    }

    public String getAtsIp() {
        return atsIp;
    }

    public String getAtsPort() {
        return atsPort;
    }

    public String getGoogleMapsKey() {
        return googleMapsKey;
    }

    public String getSmsCentrUser() {
        return smsCentrUser;
    }

    public String getSmsCentrPass() {
        return smsCentrPass;
    }

    public List<String> getAdminEmails() {
        return adminEmails;
    }

    public String getNominatimHost() {
        return nominatimHost;
    }

    public String getBankKeySecret() {
        return bankKeySecret;
    }

    public String getBankKeyPublic() {
        return bankKeyPublic;
    }

    public String getBankServiceId() {
        return bankServiceId;
    }

    public String getPushClientAPIkey() {
        return pushClientAPIkey;
    }

    public String getPushClientProjectId() {
        return pushClientProjectId;
    }

    public String getPushDriverAPIkey() {
        return pushDriverAPIkey;
    }

    public String getPushDriverProjectId() {
        return pushDriverProjectId;
    }

    public double getClientAppDefaultCost() {
        return clientAppDefaultCost;
    }

    public int getPublicNearCarsLimit() {
        return publicNearCarsLimit;
    }

    public int getMaxBonusCompPercent() {
        return maxBonusCompPercent;
    }

    public int getMaxBonusCompSum() {
        return maxBonusCompSum;
    }

    public List<String> getChatTemplates() {
        return chatTemplates;
    }

    public Weights getSearchWeights() {
        return searchWeights;
    }

    public String getDropboxPath() {
        return dropboxPath;
    }

    public long getSocketMaxIdleTimeout() {
        return socketMaxIdleTimeout;
    }

    public boolean isCloseOnDouble() {
        return closeOnDouble;
    }

    public double getQiwiWalletComission() {
        return qiwiWalletComission;
    }

    public double getQiwiTerminalComission() {
        return qiwiTerminalComission;
    }

    public String getClientOrderImpossibleString() {
        return clientOrderImpossibleString;
    }

    public boolean isCheckDriversAvalibleForClients() {
        return checkDriversAvalibleForClients;
    }

    public long getChatMessageTTL() {
        return chatMessageTTL;
    }

    public long getCallersQueueCountFetchInterval() {
        return callersQueueCountFetchInterval;
    }

    public boolean isEnableAreaExistCriteria() {
        return enableAreaExistCriteria;
    }

    public boolean isUseInmemoryAddressSearch() {
        return useInmemoryAddressSearch;
    }

    public boolean isUseSMSTranslitMsgs() {
        return useSMSTranslitMsgs;
    }

    public String getAppLink() {
        return appLink;
    }

    public boolean isUseOrderCacheForDisps() {
        return useOrderCacheForDisps;
    }

    public boolean isExcludeGeoObjectsFromSearch() {
        return excludeGeoObjectsFromSearch;
    }

    public String getRouteServiceAddres() {
        return routeServiceAddres;
    }

    public int getRouteDistanceFetchBatchSize() {
        return routeDistanceFetchBatchSize;
    }

    public int getRouteDistanceResponseWaitTimeout() {
        return routeDistanceResponseWaitTimeout;
    }

    public boolean isEnableSecureMode() {
        return enableSecureMode;
    }

    public boolean isUserGeoserviceForSuggestDistances() {
        return userGeoserviceForSuggestDistances;
    }

    public Set<Double> getExtraCosts() {
        return extraCosts;
    }

    public Set<String> getCancelCauseTypes() {
        return cancelCauseTypes;
    }

    public long getLocationUpdateExpireTimeout() {
        return locationUpdateExpireTimeout;
    }

    public double getLocationUpdateSpeed() {
        return locationUpdateSpeed;
    }

    public String getImagesFolder() {
        return imagesFolder;
    }

    public List<Integer> getDriverTimeSuggestions() {
        return driverTimeSuggestions;
    }
}
