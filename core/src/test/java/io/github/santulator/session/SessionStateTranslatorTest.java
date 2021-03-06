package io.github.santulator.session;

import io.github.santulator.core.SantaException;
import io.github.santulator.model.DrawRequirements;
import io.github.santulator.model.ParticipantRole;
import io.github.santulator.model.Person;
import io.github.santulator.model.Restriction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static io.github.santulator.core.CoreTool.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SessionStateTranslatorTest {
    private static final Person ALBERT = new Person("Albert", ParticipantRole.GIVER);

    private static final Person BERYL = new Person("Beryl", ParticipantRole.RECEIVER);

    private static final Person CARLA = new Person("Carla", ParticipantRole.BOTH);

    private final SessionStateTranslator target = new SessionStateTranslatorImpl();

    private final List<ParticipantState> participantStates = new ArrayList<>();

    @Test
    public void testEmpty() {
        validate(listOf(), listOf());
    }

    @Test
    public void testSinglePerson() {
        participant(ALBERT);
        validate(listOf(ALBERT), listOf());
    }

    @Test
    public void testThreePeople() {
        participant(ALBERT, "Beryl", "Carla");
        participant(BERYL, "Carla");
        participant(CARLA);
        validate(listOf(ALBERT, BERYL, CARLA), listOf(new Restriction(ALBERT, BERYL), new Restriction(ALBERT, CARLA), new Restriction(BERYL, CARLA)));
    }

    @Test
    public void testNonExistentPerson() {
        participant(ALBERT, "Zaphod");
        assertThrows(SantaException.class, this::runTranslation);
    }

    @Test
    public void testEmptyExclusion() {
        participant(ALBERT, "");
        assertThrows(SantaException.class, this::runTranslation);
    }

    @Test
    public void testNoName() {
        participant("", ParticipantRole.GIVER);
        assertThrows(SantaException.class, this::runTranslation);
    }

    @Test
    public void testDuplicate() {
        participant(ALBERT);
        participant(ALBERT);
        assertThrows(SantaException.class, this::runTranslation);
    }

    private void participant(final Person person, final String... exclusions) {
        participant(person.getName(), person.getRole(), exclusions);
    }

    private void participant(final String name, final ParticipantRole role, final String... exclusions) {
        participantStates.add(new ParticipantState(name, role, exclusions));
    }

    private void validate(final Collection<Person> participants, final Collection<Restriction> restrictions) {
        DrawRequirements result = runTranslation();

        assertEquals(new HashSet<>(participants), result.getParticipants());
        assertEquals(new HashSet<>(restrictions), result.getRestrictions());
    }

    private DrawRequirements runTranslation() {
        SessionState state = new SessionState();
        state.setParticipants(participantStates);

        return target.toRequirements(state);
    }
}