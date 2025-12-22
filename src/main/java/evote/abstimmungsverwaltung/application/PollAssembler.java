package evote.abstimmungsverwaltung.application;

import evote.abstimmungsverwaltung.application.dto.PollDTO;
import evote.abstimmungsverwaltung.domain.model.Poll;
import org.springframework.stereotype.Component;

@Component
public class PollAssembler {

    public PollDTO toDTO(Poll poll) {
        return new PollDTO(
                poll.getPollId(),
                poll.getTitle(),
                poll.getStartDate(),
                poll.getEndDate(),
                poll.getOptions()
        );
    }
}
