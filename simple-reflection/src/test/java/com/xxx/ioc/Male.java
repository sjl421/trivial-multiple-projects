package com.xxx.ioc;

import com.xxx.ioc.bean.MyBean;

/**
 * Created by dhy on 17-4-5.
 *
 */
@MyBean
public class Male extends Person {
    private short bust;
    private short waist;
    private short hip;

    public short getBust() {
        return bust;
    }

    public void setBust(short bust) {
        this.bust = bust;
    }

    public short getWaist() {
        return waist;
    }

    public void setWaist(short waist) {
        this.waist = waist;
    }

    public short getHip() {
        return hip;
    }

    public void setHip(short hip) {
        this.hip = hip;
    }
}
