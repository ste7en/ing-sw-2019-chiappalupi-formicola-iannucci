package it.polimi.ingsw.model.cards;

/**
 * enumeration to save the effect properties
 */
public enum EffectProperty {
    /**
     * Minimum distance from where a weapon can shoot; (-1) if the weapon can be used only against targets who can't be seen, (-2) if the weapon can be used only against targets who are in different rooms
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
    AdditionalTarget,

    /**
     * Saves if a player can move a target before using the effect
     */
    CanMoveBefore,

    /**
     * Saves if a player can move a target after using the effect; (negative) if the target must be moved in a straight direction
     */
    CanMoveAfter,

    /**
     * Saves if a player can move himself before or after the effect; (negative) if the player moves in the cell of its target
     */
    MoveMe,

    /**
     * Saves if the Weapon must shoot to different cells and can't shoot to multiple targets in a single cell; (-1) if it's an entire room, (-2) if it's an entire direction, (0) if it's everyone in a cell
     */
    MultipleCell,

    /**
     * Saves if the effect is applied from the point of view of another target; (negative) if its value refers to the effect instead of the target
     */
    EffectOnTarget,

    /**
     * Saves the damages given by the effect
     */
    Damage,

    /**
     * Saves the marks given by the effect; (negative) if the effect gives marks to all the players in a square
     */
    Mark,

    /**
     * Saves if the effect was too different from the others and defining new properties for it would be worthless (and over-engineered)
     */
    Hard
}
