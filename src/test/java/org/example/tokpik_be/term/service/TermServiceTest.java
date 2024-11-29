package org.example.tokpik_be.term.service;

import static org.mockito.BDDMockito.*;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.term.domain.Term;
import org.example.tokpik_be.term.dto.response.TermResponse;
import org.example.tokpik_be.term.repository.TermRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TermServiceTest {

	@Mock
	private TermRepository termRepository;

	@InjectMocks
	private TermService termService;

	@DisplayName("이용약관을 조회한다.")
	@Test
	void getTerm(){
	    // given
		List<Term> terms = List.of(
			new Term("콘텐츠에 대한 라이선스", 1, 1, "업로드된 콘텐츠", "이 콘텐츠에서는.."),
			new Term("콘텐츠에 대한 라이선스", 2, 1, "콘텐츠 사용 제한", "이 콘텐츠는 다음과 같이 제한..")
		);

		given(termRepository.findAll()).willReturn(terms);

	    // when
		List<TermResponse> response = termService.getAllTerms();

	    // then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response).hasSize(1);
			TermResponse termResponse = response.get(0);
			softly.assertThat(termResponse.title()).isEqualTo("콘텐츠에 대한 라이선스");
			softly.assertThat(termResponse.sections()).hasSize(2);

			softly.assertThat(termResponse.sections().get(0).mainCategory()).isEqualTo("1");
			softly.assertThat(termResponse.sections().get(0).subCategory()).isEqualTo("1");
			softly.assertThat(termResponse.sections().get(0).contentTitle()).isEqualTo("업로드된 콘텐츠");
			softly.assertThat(termResponse.sections().get(0).content()).isEqualTo("이 콘텐츠에서는..");
			softly.assertThat(termResponse.sections().get(1).mainCategory()).isEqualTo("2");
			softly.assertThat(termResponse.sections().get(1).subCategory()).isEqualTo("1");
			softly.assertThat(termResponse.sections().get(1).contentTitle()).isEqualTo("콘텐츠 사용 제한");
			softly.assertThat(termResponse.sections().get(1).content()).isEqualTo("이 콘텐츠는 다음과 같이 제한..");
		});
	}
}
