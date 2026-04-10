/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Sealed marker interface for upload packet bodies
 */
package hp.protocol;

public sealed interface PacketBody permits HeaderOnlyBody, SprintBody, SneakBody, SwimBody, AttackBody {
}
