package org.example.tokpik_be.term.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record TermResponse(

    @Schema(type = "string", description = "이용약관 전체 제목", example = "콘텐츠에 대한 라이선스")
    String title,
    List<TermsSection> sections
) {
    public record TermsSection(
        @Schema(type = "string", description = "메인 카테고리", example = "1")
        String mainCategory,
        @Schema(type = "string", description = "서브 카테고리", example = "1")
        String subCategory,
        @Schema(type = "string", description = "콘텐츠 제목", example = "업로드된 콘텐츠")
        String contentTitle,
        @Schema(type = "string", description = "콘텐츠 내용", example = "이 콘텐츠 에서는..")
        String content
    ) {}
}
