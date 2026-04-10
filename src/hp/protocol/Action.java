/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Action enum and network byte mapping for the upload protocol
 */
package hp.protocol;

public enum Action {
    NULL(-1),
    ASD(-1),
    SPRINT(0),
    SNEAK(1),
    SWIM(2),
    ATTACK(3);

    private final int networkByte;

    Action(int networkByte) {
        this.networkByte = networkByte;
    }

    public int networkByte() {
        return this.networkByte;
    }

    public boolean isNetworkEffective() {
        return this.networkByte >= 0;
    }

    public static Action decode(int value) {
        for (Action action : values()) {
            if (action.networkByte == value && action.isNetworkEffective()) {
                return action;
            }
        }
        throw new IllegalArgumentException("unsupported action byte: " + value);
    }
}
