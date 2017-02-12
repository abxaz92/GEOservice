package ru.macrobit.geoservice.pojo;

import ru.macrobit.geoservice.common.Entity;
import ru.macrobit.geoservice.search.pojo.Weights;

import java.util.*;

public class MainProperties extends Entity {
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
    private String pushClientAPIkey;
    private String pushClientProjectId;
    private String pushDriverAPIkey;
    private String pushDriverProjectId;
    private double clientAppDefaultCost;
    private int publicNearCarsLimit;

    private int maxBonusCompPercent;
    private int maxBonusCompSum;

    private List<String> chatTemplates;

    private Weights searchWeights = new Weights();
    private String dropboxPath;

    private long socketMaxIdleTimeout;
    private boolean closeOnDouble;
    private double qiwiWalletComission;
    private double qiwiTerminalComission;
    private String clientOrderImpossibleString = "Не повзможно обслужить заказ";
    private boolean checkDriversAvalibleForClients;
    private long callersQueueCountFetchInterval = 2000;


    private long chatMessageTTL = 300000;
    private boolean enableAreaExistCriteria;
    private boolean useInmemoryAddressSearch;
    private String appLink;
    private boolean useSMSTranslitMsgs;
    private boolean useOrderCacheForDisps;
    private boolean excludeGeoObjectsFromSearch;
    private String routeServiceAddres = "http://80.87.201.17:8080";
    private int routeDistanceFetchBatchSize = 20;
    private int routeDistanceResponseWaitTimeout = 20;
    private boolean enableSecureMode;
    private boolean userGeoserviceForSuggestDistances;
    private Set<Double> extraCosts = new HashSet<>();
    private Set<String> cancelCauseTypes = new HashSet<>();

    private long locationUpdateExpireTimeout = 60000;
    private double locationUpdateSpeed = 17;
    private String imagesFolder = "/www/data/images/";
    private List<Integer> driverTimeSuggestions = new ArrayList<>(Arrays.asList(2, 4, 6, 8, 10, 12, 15, 20, 30));

    public MainProperties() {
        this.adminEmails = new ArrayList<>();
        this.chatTemplates = new ArrayList<>();
    }

    public String getPushClientAPIkey() {
        return pushClientAPIkey;
    }

    public void setPushClientAPIkey(String pushClientAPIkey) {
        this.pushClientAPIkey = pushClientAPIkey;
    }

    public String getPushClientProjectId() {
        return pushClientProjectId;
    }

    public void setPushClientProjectId(String pushClientProjectId) {
        this.pushClientProjectId = pushClientProjectId;
    }

    public String getPushDriverAPIkey() {
        return pushDriverAPIkey;
    }

    public void setPushDriverAPIkey(String pushDriverAPIkey) {
        this.pushDriverAPIkey = pushDriverAPIkey;
    }

    public String getPushDriverProjectId() {
        return pushDriverProjectId;
    }

    public void setPushDriverProjectId(String pushDriverProjectId) {
        this.pushDriverProjectId = pushDriverProjectId;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getCertPass() {
        return certPass;
    }

    public void setCertPass(String certPass) {
        this.certPass = certPass;
    }

    public String getGoogleMapsKey() {
        return googleMapsKey;
    }

    public void setGoogleMapsKey(String googleMapsKey) {
        this.googleMapsKey = googleMapsKey;
    }

    public String getSmsCentrUser() {
        return smsCentrUser;
    }

    public void setSmsCentrUser(String smsCentrUser) {
        this.smsCentrUser = smsCentrUser;
    }

    public String getSmsCentrPass() {
        return smsCentrPass;
    }

    public void setSmsCentrPass(String smsCentrPass) {
        this.smsCentrPass = smsCentrPass;
    }

    public List<String> getAdminEmails() {
        return adminEmails;
    }

    public void setAdminEmails(List<String> adminEmails) {
        this.adminEmails = adminEmails;
    }

    public String getNominatimHost() {
        return nominatimHost;
    }

    public void setNominatimHost(String nominatimHost) {
        this.nominatimHost = nominatimHost;
    }

    public String getBankKeySecret() {
        return bankKeySecret;
    }

    public void setBankKeySecret(String bankKeySecret) {
        this.bankKeySecret = bankKeySecret;
    }

    public String getBankKeyPublic() {
        return bankKeyPublic;
    }

    public void setBankKeyPublic(String bankKeyPublic) {
        this.bankKeyPublic = bankKeyPublic;
    }

    public String getBankServiceId() {
        return bankServiceId;
    }

    public void setBankServiceId(String bankServiceId) {
        this.bankServiceId = bankServiceId;
    }

    public String getAtsIp() {
        return atsIp;
    }

    public void setAtsIp(String atsIp) {
        this.atsIp = atsIp;
    }

    public String getAtsPort() {
        return atsPort;
    }

    public void setAtsPort(String atsPort) {
        this.atsPort = atsPort;
    }

    public double getClientAppDefaultCost() {
        return clientAppDefaultCost;
    }

    public void setClientAppDefaultCost(double clientAppDefaultCost) {
        this.clientAppDefaultCost = clientAppDefaultCost;
    }

    public int getPublicNearCarsLimit() {
        return publicNearCarsLimit;
    }

    public void setPublicNearCarsLimit(int publicNearCarsLimit) {
        this.publicNearCarsLimit = publicNearCarsLimit;
    }

    public int getMaxBonusCompPercent() {
        return maxBonusCompPercent;
    }

    public void setMaxBonusCompPercent(int maxBonusCompPercent) {
        this.maxBonusCompPercent = maxBonusCompPercent;
    }

    public int getMaxBonusCompSum() {
        return maxBonusCompSum;
    }

    public void setMaxBonusCompSum(int maxBonusCompSum) {
        this.maxBonusCompSum = maxBonusCompSum;
    }

    public List<String> getChatTemplates() {
        return chatTemplates;
    }

    public void setChatTemplates(List<String> chatTemplates) {
        this.chatTemplates = chatTemplates;
    }

    public Weights getSearchWeights() {
        return searchWeights;
    }

    public void setSearchWeights(Weights searchWeights) {
        this.searchWeights = searchWeights;
    }

    public String getDropboxPath() {
        return dropboxPath;
    }

    public void setDropboxPath(String dropboxPath) {
        this.dropboxPath = dropboxPath;
    }

    public long getSocketMaxIdleTimeout() {
        return socketMaxIdleTimeout;
    }

    public void setSocketMaxIdleTimeout(long socketMaxIdleTimeout) {
        this.socketMaxIdleTimeout = socketMaxIdleTimeout;
    }

    public boolean isCloseOnDouble() {
        return closeOnDouble;
    }

    public void setCloseOnDouble(boolean closeOnDouble) {
        this.closeOnDouble = closeOnDouble;
    }

    public double getQiwiWalletComission() {
        return qiwiWalletComission;
    }

    public void setQiwiWalletComission(double qiwiWalletComission) {
        this.qiwiWalletComission = qiwiWalletComission;
    }

    public double getQiwiTerminalComission() {
        return qiwiTerminalComission;
    }

    public void setQiwiTerminalComission(double qiwiTerminalComission) {
        this.qiwiTerminalComission = qiwiTerminalComission;
    }

    public String getClientOrderImpossibleString() {
        return clientOrderImpossibleString;
    }

    public void setClientOrderImpossibleString(String clientOrderImpossibleString) {
        this.clientOrderImpossibleString = clientOrderImpossibleString;
    }

    public boolean isCheckDriversAvalibleForClients() {
        return checkDriversAvalibleForClients;
    }

    public void setCheckDriversAvalibleForClients(boolean checkDriversAvalibleForClients) {
        this.checkDriversAvalibleForClients = checkDriversAvalibleForClients;
    }

    public long getChatMessageTTL() {
        return chatMessageTTL;
    }

    public void setChatMessageTTL(long chatMessageTTL) {
        this.chatMessageTTL = chatMessageTTL;
    }

    public long getCallersQueueCountFetchInterval() {
        return callersQueueCountFetchInterval;
    }

    public void setCallersQueueCountFetchInterval(long callersQueueCountFetchInterval) {
        this.callersQueueCountFetchInterval = callersQueueCountFetchInterval;
    }

    public boolean isEnableAreaExistCriteria() {
        return enableAreaExistCriteria;
    }

    public void setEnableAreaExistCriteria(boolean enableAreaExistCriteria) {
        this.enableAreaExistCriteria = enableAreaExistCriteria;
    }

    public boolean isUseInmemoryAddressSearch() {
        return useInmemoryAddressSearch;
    }

    public void setUseInmemoryAddressSearch(boolean useInmemoryAddressSearch) {
        this.useInmemoryAddressSearch = useInmemoryAddressSearch;
    }

    public String getAppLink() {
        return appLink;
    }

    public void setAppLink(String appLink) {
        this.appLink = appLink;
    }

    public boolean isUseSMSTranslitMsgs() {
        return useSMSTranslitMsgs;
    }

    public void setUseSMSTranslitMsgs(boolean useSMSTranslitMsgs) {
        this.useSMSTranslitMsgs = useSMSTranslitMsgs;
    }

    public boolean isUseOrderCacheForDisps() {
        return useOrderCacheForDisps;
    }

    public void setUseOrderCacheForDisps(boolean useOrderCacheForDisps) {
        this.useOrderCacheForDisps = useOrderCacheForDisps;
    }

    public boolean isExcludeGeoObjectsFromSearch() {
        return excludeGeoObjectsFromSearch;
    }

    public void setExcludeGeoObjectsFromSearch(boolean excludeGeoObjectsFromSearch) {
        this.excludeGeoObjectsFromSearch = excludeGeoObjectsFromSearch;
    }

    public String getRouteServiceAddres() {
        return routeServiceAddres;
    }

    public void setRouteServiceAddres(String routeServiceAddres) {
        this.routeServiceAddres = routeServiceAddres;
    }

    public int getRouteDistanceFetchBatchSize() {
        return routeDistanceFetchBatchSize;
    }

    public void setRouteDistanceFetchBatchSize(int routeDistanceFetchBatchSize) {
        this.routeDistanceFetchBatchSize = routeDistanceFetchBatchSize;
    }

    public int getRouteDistanceResponseWaitTimeout() {
        return routeDistanceResponseWaitTimeout;
    }

    public void setRouteDistanceResponseWaitTimeout(int routeDistanceResponseWaitTimeout) {
        this.routeDistanceResponseWaitTimeout = routeDistanceResponseWaitTimeout;
    }

    public boolean isEnableSecureMode() {
        return enableSecureMode;
    }

    public void setEnableSecureMode(boolean enableSecureMode) {
        this.enableSecureMode = enableSecureMode;
    }

    public boolean isUserGeoserviceForSuggestDistances() {
        return userGeoserviceForSuggestDistances;
    }

    public void setUserGeoserviceForSuggestDistances(boolean userGeoserviceForSuggestDistances) {
        this.userGeoserviceForSuggestDistances = userGeoserviceForSuggestDistances;
    }

    public Set<Double> getExtraCosts() {
        return extraCosts;
    }

    public void setExtraCosts(Set<Double> extraCosts) {
        this.extraCosts = extraCosts;
    }

    public Set<String> getCancelCauseTypes() {
        return cancelCauseTypes;
    }

    public void setCancelCauseTypes(Set<String> cancelCauseTypes) {
        this.cancelCauseTypes = cancelCauseTypes;
    }

    public long getLocationUpdateExpireTimeout() {
        return locationUpdateExpireTimeout;
    }

    public void setLocationUpdateExpireTimeout(long locationUpdateExpireTimeout) {
        this.locationUpdateExpireTimeout = locationUpdateExpireTimeout;
    }

    public double getLocationUpdateSpeed() {
        return locationUpdateSpeed;
    }

    public void setLocationUpdateSpeed(double locationUpdateSpeed) {
        this.locationUpdateSpeed = locationUpdateSpeed;
    }

    public String getImagesFolder() {
        return imagesFolder;
    }

    public void setImagesFolder(String imagesFolder) {
        this.imagesFolder = imagesFolder;
    }

    public List<Integer> getDriverTimeSuggestions() {
        return driverTimeSuggestions;
    }

    public void setDriverTimeSuggestions(List<Integer> driverTimeSuggestions) {
        this.driverTimeSuggestions = driverTimeSuggestions;
    }
}
