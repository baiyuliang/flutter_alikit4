package mtopsdk.mtop.intf;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taobao.tao.remotebusiness.RequestPoolManager;
import com.taobao.tao.remotebusiness.login.IRemoteLogin;
import com.taobao.tao.remotebusiness.login.RemoteLogin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import anetwork.network.cache.Cache;
import mtopsdk.common.util.MtopUtils;
import mtopsdk.common.util.StringUtils;
import mtopsdk.common.util.TBSdkLog;
import mtopsdk.mtop.domain.EnvModeEnum;
import mtopsdk.mtop.domain.IMTOPDataObject;
import mtopsdk.mtop.domain.MtopRequest;
import mtopsdk.mtop.global.MtopConfig;
import mtopsdk.mtop.global.SwitchConfig;
import mtopsdk.mtop.global.init.IMtopInitTask;
import mtopsdk.mtop.network.NetworkPropertyService;
import mtopsdk.mtop.util.MtopSDKThreadPoolExecutorFactory;
import mtopsdk.xstate.XState;
import mtopsdk.xstate.util.XStateConstants;

/* loaded from: mtopsdk_allinone_open-3.1.22-opt.jar:mtopsdk/mtop/intf/Mtop.class */
public class Mtop {
    public static final String CHANNEL_PROCESS_NAME = "com.taobao.taobao:channel";
    public static final String WIDGET_PROCESS_NAME = "com.taobao.taobao:widget";
    public static final String XIAOMI_WIDGET_PROCESS_NAME = "com.taobao.taobao:widgetProvider";
    private static final String TAG = "mtopsdk.Mtop";
    private Map<String, MtopBuilder> prefetchBuilderMap;
    private static final int MAX_PREFETCH_LENGTH = 50;
    public volatile long lastPrefetchResponseTime;
    private String instanceId;
    MtopConfig mtopConfig;
    IMtopInitTask initTask;
    private volatile boolean isInit;
    volatile boolean isInited;
    final byte[] initLock;
    private int type;
    private static final String MTOP_ID_XIANYU = "MTOP_ID_XIANYU";
    private static final String MTOP_SITE_XIANYU = "xianyu";
    private static final String MTOP_ID_KOUBEI = "MTOP_ID_KOUBEI";
    private static final String MTOP_SITE_KOUBEI = "koubei";
    private static final String MTOP_ID_ELEME = "MTOP_ID_ELEME";
    private static final String MTOP_SITE_ELEME = "eleme";
    private static final String MTOP_ID_INNER = "INNER";
    private static final String MTOP_SITE_INNER = "taobao";
    public static final String MTOP_ID_TAOBAO = "MTOP_ID_TAOBAO";
    public static boolean mIsFullTrackValid = false;
    protected static Map<String, Mtop> instanceMap = new ConcurrentHashMap();

    /* renamed from: mtopsdk.mtop.intf.Mtop$Id */
    /* loaded from: mtopsdk_allinone_open-3.1.22-opt.jar:mtopsdk/mtop/intf/Mtop$Id.class */
    public interface InterfaceC0026Id {
        public static final String OPEN = "OPEN";
        public static final String INNER = "INNER";
        public static final String PRODUCT = "PRODUCT";
        public static final String CUTE = "CUTE";

        @Retention(RetentionPolicy.SOURCE)
        /* renamed from: mtopsdk.mtop.intf.Mtop$Id$Definition */
        /* loaded from: mtopsdk_allinone_open-3.1.22-opt.jar:mtopsdk/mtop/intf/Mtop$Id$Definition.class */
        public @interface Definition {
        }
    }

    /* loaded from: mtopsdk_allinone_open-3.1.22-opt.jar:mtopsdk/mtop/intf/Mtop$Site.class */
    public interface Site {
        public static final String TAOBAO = "taobao";
        public static final String XIANYU = "xianyu";
        public static final String ELEME = "eleme";

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: mtopsdk_allinone_open-3.1.22-opt.jar:mtopsdk/mtop/intf/Mtop$Site$Definition.class */
        public @interface Definition {
        }
    }

    /* loaded from: mtopsdk_allinone_open-3.1.22-opt.jar:mtopsdk/mtop/intf/Mtop$Type.class */
    public interface Type {
        public static final int INNER = 0;
        public static final int OPEN = 1;
        public static final int PRODUCT = 2;
        public static final int CUTE = 3;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: mtopsdk_allinone_open-3.1.22-opt.jar:mtopsdk/mtop/intf/Mtop$Type$Definition.class */
        public @interface Definition {
        }
    }

    private Mtop(String instanceId, @NonNull MtopConfig mtopConfig) {
        this.prefetchBuilderMap = new ConcurrentHashMap();
        this.lastPrefetchResponseTime = System.currentTimeMillis();
        this.isInit = false;
        this.isInited = false;
        this.initLock = new byte[0];
        this.type = 0;
        this.instanceId = instanceId;
        this.mtopConfig = mtopConfig;

        try {
            Class<?> clazz = Class.forName("mtopsdk.mtop.global.init.OpenMtopInitTask");
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            initTask = (IMtopInitTask) constructor.newInstance();
        } catch (Throwable var4) {
        }

        if (null == this.initTask) {
            throw new RuntimeException("IMtopInitTask is null,instanceId=" + instanceId);
        }
        try {
            Class.forName("com.taobao.analysis.fulltrace.FullTraceAnalysis");
            Class.forName("com.taobao.analysis.scene.SceneIdentifier");
            Class.forName("com.taobao.analysis.abtest.ABTestCenter");
            Class.forName("com.taobao.analysis.v3.FalcoGlobalTracer");
            mIsFullTrackValid = true;
        } catch (Throwable th) {
            mIsFullTrackValid = false;
        }
    }

    @Deprecated
    public static Mtop instance(Context context) {
        return instance(null, context, null);
    }

    @Deprecated
    public static Mtop instance(Context context, String ttid) {
        return instance(null, context, ttid);
    }

    public static Mtop instance(String instanceId, @NonNull Context context) {
        return instance(instanceId, context, null);
    }

    private static void channelLazyConfig(Context context, Mtop instance) {
        if (SwitchConfig.getInstance().getEnableChannelLazy() && CHANNEL_PROCESS_NAME.equals(MtopUtils.getCurrentProcessName(context))) {
            String id = instance.getInstanceId();
            if ("INNER".equals(id)) {
                MtopAccountSiteUtils.bindInstanceId("INNER", "taobao");
                return;
            }
            String accountSite = "";
            String instanceId = "";
            if (MTOP_ID_ELEME.equals(id)) {
                instanceId = MTOP_ID_ELEME;
                accountSite = "eleme";
            } else if (MTOP_ID_XIANYU.equals(id)) {
                instanceId = MTOP_ID_XIANYU;
                accountSite = "xianyu";
            } else if (MTOP_ID_KOUBEI.equals(id)) {
                instanceId = MTOP_ID_KOUBEI;
                accountSite = MTOP_SITE_KOUBEI;
            }
            if (TextUtils.isEmpty(instanceId) || TextUtils.isEmpty(accountSite)) {
                return;
            }
            MtopAccountSiteUtils.bindInstanceId(instanceId, accountSite);
            try {
                Class<?> threadClazz = Class.forName("com.ali.user.open.mtop.UccRemoteLogin");
                Method method = threadClazz.getMethod("getUccLoginImplWithSite", String.class);
                RemoteLogin.setLoginImpl(instance, (IRemoteLogin) method.invoke(null, accountSite));
            } catch (ClassNotFoundException e) {
                TBSdkLog.e(TAG, e.toString());
            } catch (IllegalAccessException e2) {
                TBSdkLog.e(TAG, e2.toString());
            } catch (NoSuchMethodException e3) {
                TBSdkLog.e(TAG, e3.toString());
            } catch (InvocationTargetException e4) {
                TBSdkLog.e(TAG, e4.toString());
            }
        }
    }

    public static Mtop instance(String instanceId, @NonNull Context context, String ttid) {
        String id = StringUtils.isNotBlank(instanceId) ? instanceId : "INNER";
        Mtop instance = instanceMap.get(id);
        if (instance == null) {
            synchronized (Mtop.class) {
                instance = instanceMap.get(id);
                if (instance == null) {
                    MtopConfig mtopConfig = MtopSetting.mtopConfigMap.get(id);
                    if (null == mtopConfig) {
                        mtopConfig = new MtopConfig(id);
                    }
                    instance = new Mtop(id, mtopConfig);
                    mtopConfig.mtopInstance = instance;
                    instanceMap.put(id, instance);
                    channelLazyConfig(context, instance);
                }
            }
        }
        if (!instance.isInit) {
            instance.init(context, ttid);
        }
        return instance;
    }

    private Mtop(String instanceId, int type, @NonNull MtopConfig mtopConfig) {
        this.prefetchBuilderMap = new ConcurrentHashMap();
        this.lastPrefetchResponseTime = System.currentTimeMillis();
        this.isInit = false;
        this.isInited = false;
        this.initLock = new byte[0];
        this.type = 0;
        this.instanceId = instanceId;
        this.mtopConfig = mtopConfig;
        this.type = type;
        try {
            Class<?> clazz = Class.forName("mtopsdk.mtop.global.init.OpenMtopInitTask");
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            initTask = (IMtopInitTask) constructor.newInstance();
        } catch (Throwable var4) {
        }
        if (null == this.initTask) {
            throw new RuntimeException("IMtopInitTask is null,instanceId=" + instanceId);
        }
        try {
            Class.forName("com.taobao.analysis.fulltrace.FullTraceAnalysis");
            Class.forName("com.taobao.analysis.scene.SceneIdentifier");
            Class.forName("com.taobao.analysis.abtest.ABTestCenter");
            Class.forName("com.taobao.analysis.v3.FalcoGlobalTracer");
            mIsFullTrackValid = true;
        } catch (Throwable th) {
            mIsFullTrackValid = false;
        }
    }

    public static Mtop instance(String instanceId, @NonNull Context context, String ttid, int type) {
        String id = StringUtils.isNotBlank(instanceId) ? instanceId : "INNER";
        Mtop instance = instanceMap.get(id);
        if (instance == null) {
            synchronized (Mtop.class) {
                instance = instanceMap.get(id);
                if (instance == null) {
                    MtopConfig mtopConfig = MtopSetting.mtopConfigMap.get(id);
                    if (null == mtopConfig) {
                        mtopConfig = new MtopConfig(id);
                    }
                    instance = new Mtop(id, type, mtopConfig);
                    mtopConfig.mtopInstance = instance;
                    instanceMap.put(id, instance);
                    channelLazyConfig(context, instance);
                }
            }
        }
        if (!instance.isInit) {
            instance.init(context, ttid);
        }
        return instance;
    }

    public static Mtop instance(String instanceId, @NonNull Context context, String ttid, int type, MtopConfig config) {
        String id = StringUtils.isNotBlank(instanceId) ? instanceId : "INNER";
        Mtop instance = instanceMap.get(id);
        if (instance == null) {
            synchronized (Mtop.class) {
                instance = instanceMap.get(id);
                if (instance == null) {
                    MtopConfig mtopConfig = MtopSetting.mtopConfigMap.get(id);
                    if (null == mtopConfig) {
                        if (null != config) {
                            mtopConfig = config;
                        } else {
                            mtopConfig = new MtopConfig(id);
                        }
                    }
                    instance = new Mtop(id, type, mtopConfig);
                    mtopConfig.mtopInstance = instance;
                    instanceMap.put(id, instance);
                    channelLazyConfig(context, instance);
                }
            }
        }
        if (!instance.isInit) {
            instance.init(context, ttid);
        }
        return instance;
    }

    @Nullable
    public static Mtop getInstance(String instanceId) {
        return getMtopInstance(instanceId);
    }

    @Deprecated
    public static Mtop getMtopInstance(String instanceId) {
        String id = StringUtils.isNotBlank(instanceId) ? instanceId : "INNER";
        return instanceMap.get(id);
    }

    @Nullable
    public static Mtop getInstanceWithAccountSite(String site) {
        String instanceId = MtopAccountSiteUtils.getInstanceId(site);
        if (StringUtils.isBlank(instanceId)) {
            return null;
        }
        return getMtopInstance(instanceId);
    }

    public int getType() {
        return this.type;
    }

    public String getInstanceId() {
        return this.instanceId;
    }

    public MtopConfig getMtopConfig() {
        return this.mtopConfig;
    }

    private synchronized void init(Context context, String ttid) {
        if (this.isInit) {
            return;
        }
        if (context == null) {
            TBSdkLog.e(TAG, this.instanceId + " [init] The Parameter context can not be null.");
            return;
        }
        if (TBSdkLog.isLogEnable(TBSdkLog.LogEnable.InfoEnable)) {
            TBSdkLog.e(TAG, this.instanceId + " [init] context=" + context + ", ttid=" + ttid);
        }
        this.mtopConfig.context = context.getApplicationContext();
        if (StringUtils.isNotBlank(ttid)) {
            this.mtopConfig.ttid = ttid;
        }
        MtopSDKThreadPoolExecutorFactory.submit(new Runnable() { // from class: mtopsdk.mtop.intf.Mtop.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    synchronized (Mtop.this.initLock) {
                        long startTime = System.currentTimeMillis();
                        try {
                            Mtop.this.updateAppKeyIndex();
                            Mtop.this.initTask.executeCoreTask(Mtop.this.mtopConfig);
                            MtopSDKThreadPoolExecutorFactory.submit(new Runnable() { // from class: mtopsdk.mtop.intf.Mtop.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    try {
                                        Mtop.this.initTask.executeExtraTask(Mtop.this.mtopConfig);
                                    } catch (Throwable e) {
                                        TBSdkLog.e(Mtop.TAG, Mtop.this.instanceId + " [init] executeExtraTask error.", e);
                                    }
                                }
                            });
                            TBSdkLog.e(Mtop.TAG, Mtop.this.instanceId + " [init]do executeCoreTask cost[ms]: " + (System.currentTimeMillis() - startTime));
                            Mtop.this.isInited = true;
                            Mtop.this.initLock.notifyAll();
                        } catch (Throwable th) {
                            TBSdkLog.e(Mtop.TAG, Mtop.this.instanceId + " [init]do executeCoreTask cost[ms]: " + (System.currentTimeMillis() - startTime));
                            Mtop.this.isInited = true;
                            Mtop.this.initLock.notifyAll();
                            throw th;
                        }
                    }
                } catch (Exception e) {
                    TBSdkLog.e(Mtop.TAG, Mtop.this.instanceId + " [init] executeCoreTask error.", e);
                }
            }
        });
        this.isInit = true;
    }

    public void unInit() {
        this.isInited = false;
        this.isInit = false;
        if (TBSdkLog.isLogEnable(TBSdkLog.LogEnable.InfoEnable)) {
            TBSdkLog.e(TAG, this.instanceId + "[unInit] MTOPSDK unInit called");
        }
    }

    void updateAppKeyIndex() {
        EnvModeEnum envMode = this.mtopConfig.envMode;
        if (null == envMode) {
            return;
        }
        switch (envMode) {
            case ONLINE:
            case PREPARE:
                this.mtopConfig.appKeyIndex = this.mtopConfig.onlineAppKeyIndex;
                return;
            case TEST:
            case TEST_SANDBOX:
                this.mtopConfig.appKeyIndex = this.mtopConfig.dailyAppkeyIndex;
                return;
            default:
                return;
        }
    }

    public Mtop switchEnvMode(final EnvModeEnum envMode) {
        if (envMode == null || this.mtopConfig.envMode == envMode) {
            return this;
        }
        if (!MtopUtils.isApkDebug(this.mtopConfig.context) && !this.mtopConfig.isAllowSwitchEnv.compareAndSet(true, false)) {
            TBSdkLog.e(TAG, this.instanceId + " [switchEnvMode]release package can switch environment only once!");
            return this;
        }
        if (TBSdkLog.isLogEnable(TBSdkLog.LogEnable.InfoEnable)) {
            TBSdkLog.e(TAG, this.instanceId + " [switchEnvMode]MtopSDK switchEnvMode called.envMode=" + envMode);
        }
        Runnable switchEnvTask = new Runnable() { // from class: mtopsdk.mtop.intf.Mtop.2
            @Override // java.lang.Runnable
            public void run() {
                Mtop.this.checkMtopSDKInit();
                if (Mtop.this.mtopConfig.envMode == envMode) {
                    TBSdkLog.e(Mtop.TAG, Mtop.this.instanceId + " [switchEnvMode] Current EnvMode matches target EnvMode,envMode=" + envMode);
                    return;
                }
                if (TBSdkLog.isLogEnable(TBSdkLog.LogEnable.InfoEnable)) {
                    TBSdkLog.e(Mtop.TAG, Mtop.this.instanceId + " [switchEnvMode]MtopSDK switchEnvMode start");
                }
                Mtop.this.mtopConfig.envMode = envMode;
                try {
                    Mtop.this.updateAppKeyIndex();
                    if (EnvModeEnum.ONLINE == envMode) {
                        TBSdkLog.setPrintLog(false);
                    }
                    Mtop.this.initTask.executeCoreTask(Mtop.this.mtopConfig);
                    Mtop.this.initTask.executeExtraTask(Mtop.this.mtopConfig);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (TBSdkLog.isLogEnable(TBSdkLog.LogEnable.InfoEnable)) {
                    TBSdkLog.e(Mtop.TAG, Mtop.this.instanceId + " [switchEnvMode]MtopSDK switchEnvMode end. envMode =" + envMode);
                }
            }
        };
        MtopSDKThreadPoolExecutorFactory.submit(switchEnvTask);
        return this;
    }

    public boolean checkMtopSDKInit() {
        if (this.isInited) {
            return this.isInited;
        }
        synchronized (this.initLock) {
            try {
                if (!this.isInited) {
                    this.initLock.wait(60000L);
                    if (!this.isInited) {
                        TBSdkLog.e(TAG, this.instanceId + " [checkMtopSDKInit]Didn't call Mtop.instance(...),please execute global init.");
                    }
                }
            } catch (Exception e) {
                TBSdkLog.e(TAG, this.instanceId + " [checkMtopSDKInit] wait Mtop initLock failed---" + e.toString());
            }
        }
        return this.isInited;
    }

    public boolean isInited() {
        return this.isInited;
    }

    @Deprecated
    public Mtop registerSessionInfo(String sid, @Deprecated String ecode, String userId) {
        return registerMultiAccountSession(null, sid, userId);
    }

    @Deprecated
    public static void setAppKeyIndex(int onlineIndex, int dailyIndex) {
        MtopSetting.setAppKeyIndex(onlineIndex, dailyIndex);
    }

    @Deprecated
    public static void setAppVersion(String appVersion) {
        MtopSetting.setAppVersion(appVersion);
    }

    @Deprecated
    public static void setSecurityAppKey(String securityAppKey) {
        MtopSetting.setSecurityAppKey(securityAppKey);
    }

    @Deprecated
    public static void setMtopDomain(String onlineDomain, String preDomain, String dailyDomain) {
        MtopSetting.setMtopDomain(onlineDomain, preDomain, dailyDomain);
    }

    public Mtop registerSessionInfo(String sid, String userId) {
        return registerMultiAccountSession(null, sid, userId);
    }

    public Mtop logout() {
        return logoutMultiAccountSession(null);
    }

    public Mtop registerMultiAccountSession(@Nullable String userInfo, String sid, String userId) {
        String fullUserInfo = StringUtils.concatStr(this.instanceId, StringUtils.isBlank(userInfo) ? RequestPoolManager.Type.DEFAULT : userInfo);
        XState.setValue(fullUserInfo, XStateConstants.KEY_SID, sid);
        XState.setValue(fullUserInfo, XStateConstants.KEY_UID, userId);
        if (TBSdkLog.isLogEnable(TBSdkLog.LogEnable.InfoEnable)) {
            StringBuilder builder = new StringBuilder(64);
            builder.append(fullUserInfo);
            builder.append(" [registerSessionInfo]register sessionInfo succeed: sid=").append(sid);
            builder.append(",uid=").append(userId);
            TBSdkLog.e(TAG, builder.toString());
        }
        NetworkPropertyService networkPropertyService = this.mtopConfig.networkPropertyService;
        if (networkPropertyService != null) {
            networkPropertyService.setUserId(userId);
        }
        return this;
    }

    public Mtop logoutMultiAccountSession(@Nullable String userInfo) {
        String fullUserInfo = StringUtils.concatStr(this.instanceId, StringUtils.isBlank(userInfo) ? RequestPoolManager.Type.DEFAULT : userInfo);
        XState.removeKey(fullUserInfo, XStateConstants.KEY_SID);
        XState.removeKey(fullUserInfo, XStateConstants.KEY_UID);
        if (TBSdkLog.isLogEnable(TBSdkLog.LogEnable.InfoEnable)) {
            StringBuilder builder = new StringBuilder(32);
            builder.append(fullUserInfo).append(" [logout] remove sessionInfo succeed.");
            TBSdkLog.e(TAG, builder.toString());
        }
        NetworkPropertyService networkPropertyService = this.mtopConfig.networkPropertyService;
        if (null != networkPropertyService) {
            networkPropertyService.setUserId(null);
        }
        return this;
    }

    public Mtop registerTtid(String ttid) {
        if (null != ttid) {
            this.mtopConfig.ttid = ttid;
            XState.setValue(this.instanceId, "ttid", ttid);
            NetworkPropertyService networkPropertyService = this.mtopConfig.networkPropertyService;
            if (null != networkPropertyService) {
                networkPropertyService.setTtid(ttid);
            }
        }
        return this;
    }

    public Mtop registerUtdid(String utdid) {
        if (null != utdid) {
            this.mtopConfig.utdid = utdid;
            XState.setValue(XStateConstants.KEY_UTDID, utdid);
        }
        return this;
    }

    public Mtop registerDeviceId(String deviceId) {
        if (null != deviceId) {
            this.mtopConfig.deviceId = deviceId;
            XState.setValue(this.instanceId, XStateConstants.KEY_DEVICEID, deviceId);
        }
        return this;
    }

    @Deprecated
    public String getSid() {
        return getMultiAccountSid(null);
    }

    public String getMultiAccountSid(String userInfo) {
        return XState.getValue(StringUtils.concatStr(this.instanceId, StringUtils.isBlank(userInfo) ? RequestPoolManager.Type.DEFAULT : userInfo), XStateConstants.KEY_SID);
    }

    @Deprecated
    public String getUserId() {
        return getMultiAccountUserId(null);
    }

    public String getMultiAccountUserId(String userInfo) {
        return XState.getValue(StringUtils.concatStr(this.instanceId, StringUtils.isBlank(userInfo) ? RequestPoolManager.Type.DEFAULT : userInfo), XStateConstants.KEY_UID);
    }

    public String getTtid() {
        return XState.getValue(this.instanceId, "ttid");
    }

    public String getDeviceId() {
        return XState.getValue(this.instanceId, XStateConstants.KEY_DEVICEID);
    }

    public String getUtdid() {
        return XState.getValue(XStateConstants.KEY_UTDID);
    }

    public Mtop setCoordinates(String longitude, String latitude) {
        XState.setValue(XStateConstants.KEY_LNG, longitude);
        XState.setValue(XStateConstants.KEY_LAT, latitude);
        return this;
    }

    public boolean removeCacheBlock(String blockName) {
        Cache cache = this.mtopConfig.cacheImpl;
        return null != cache && cache.remove(blockName);
    }

    public boolean unintallCacheBlock(String blockName) {
        Cache cache = this.mtopConfig.cacheImpl;
        return null != cache && cache.uninstall(blockName);
    }

    public boolean removeCacheItem(String blockName, String cacheKey) {
        if (StringUtils.isBlank(cacheKey)) {
            TBSdkLog.e(TAG, "[removeCacheItem] remove CacheItem failed,invalid cacheKey=" + cacheKey);
            return false;
        }
        Cache cache = this.mtopConfig.cacheImpl;
        return null != cache && cache.remove(blockName, cacheKey);
    }

    public Mtop logSwitch(boolean open) {
        TBSdkLog.setPrintLog(open);
        return this;
    }

    public MtopBuilder build(IMTOPDataObject mtopData, String ttid) {
        return new MtopBuilder(this, mtopData, ttid);
    }

    public MtopBuilder build(MtopRequest request, String ttid) {
        return new MtopBuilder(this, request, ttid);
    }

    @Deprecated
    public MtopBuilder build(Object inputDo, String ttid) {
        return new MtopBuilder(this, inputDo, ttid);
    }

    public Map<String, MtopBuilder> getPrefetchBuilderMap() {
        return this.prefetchBuilderMap;
    }

    public void addPrefetchBuilderToMap(@NonNull MtopBuilder builder, String key) {
        if (this.prefetchBuilderMap.size() >= MAX_PREFETCH_LENGTH) {
            MtopPrefetch.cleanPrefetchCache(builder.mtopInstance);
        }
        if (this.prefetchBuilderMap.size() >= MAX_PREFETCH_LENGTH) {
            MtopPrefetch.onPrefetchAndCommit(MtopPrefetch.IPrefetchCallback.PrefetchCallbackType.TYPE_FULL, builder.getMtopPrefetch(), builder.getMtopContext(), null);
        }
        this.prefetchBuilderMap.put(key, builder);
    }
}
