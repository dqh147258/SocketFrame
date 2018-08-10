package com.yxf.socketframe;

public class Profile {

    private static volatile Profile sProfile;

    public static Profile getInstance() {
        if (sProfile == null) {
            synchronized (Profile.class) {
                if (sProfile == null) {
                    sProfile = new Profile();
                }
            }
        }
        return sProfile;
    }

    //------------------------------------------------


    private static final int DEFAULT_RE_CONNECT_COUNT = 3;
    private int mReConnectMaxCount = DEFAULT_RE_CONNECT_COUNT;


    public static final int VERSION_FIRST = 1;

    private static final int DEFAULT_VERSION = VERSION_FIRST;
    private int mVersion = DEFAULT_VERSION;

    private boolean mIsHeartBeatOn = true;

    private static final byte[] DEFAULT_DIVIDE = new byte[]{'C', 'r', 'L', 'f'};
    private byte[] mDivide = DEFAULT_DIVIDE;

    private Profile() {

    }

    public int getReConnectMaxCount() {
        return mReConnectMaxCount;
    }

    public void setReConnectMaxCount(int reConnectMaxCount) {
        this.mReConnectMaxCount = reConnectMaxCount;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int version) {
        this.mVersion = version;
    }

    public boolean isHeartBeatOn() {
        return mIsHeartBeatOn;
    }

    public void setHeartBeatOn(boolean heartBeatOn) {
        mIsHeartBeatOn = heartBeatOn;
    }

    public byte[] getDivide() {
        return mDivide;
    }

    public void setDivide(byte[] divide) {
        mDivide = divide;
    }
}
