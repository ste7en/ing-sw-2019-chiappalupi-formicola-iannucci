package it.polimi.ingsw;

/**
 * enumeration to save the effect properties
 */
public enum EffectProperty {
    /**
     * Minimum distance from where a weapon can shoot; (-1) if the weapon can be used only against targets who can't be seen
     */
    MinDistance,

    /**
     * Maximum distance where a weapon can shoot; (-1) if the weapon can be used only against targets who can be seen
     */
    MaxDistance,

    /**
     * Maximum number of players that can be shot through the effect; (-1) if all players must be shot
     */
    MaxPlayer,

    /**
     * Saves if the target/s must be different from the others effects of the weapon; (-1) if target/s must be the same
     */
    DifferentFromEffect,

    /**
     * Saves if a player can move a target before using the effect
     */
    CanMoveBefore,

    /**
     * Saves if a player can move a target after using the effect
     */
    CanMoveAfter,

    /**
     * Saves if a player can move himself before or after the effect
     */
    MoveMe,

    /**
     * Saves if the Weapon must shoot to different cells; (-1) if it's an entire room, (-2) if it's an entire direction, (0) if it's everyone in a cell
     */
    MultipleCell,

    /**
     * Saves if the effect is applied from the point of view of another target
     */
    EffectOnTarget,

    /**
     * Saves if marks must be given to different targets than the ones damaged
     */
    MarksAfter
}
