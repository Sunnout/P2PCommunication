package protocols.membership.hyparview.timers;

import babel.generic.ProtoTimer;

public class ShuffleTimer extends ProtoTimer{

    public static final short TIMER_ID = 151;

    public ShuffleTimer() {
        super(TIMER_ID);
    }

    @Override
    public ProtoTimer clone() {
        return this;
    }
}
