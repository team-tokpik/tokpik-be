package org.example.tokpik_be.term.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record TermResponse(

    @Schema(type = "string", description = "이용약관", example = "콘텐츠에 대한 라이선스")
    String title,
    List<TermsSection> sections
) {
    public record TermsSection(

        @Schema(type = "number", description = "메인 카테고리", example = "1")
        String mainCategory,
        @Schema(type = "number", description = "서브 카테고리", example = "2")
        String subCategory,
        @Schema(type = "string", description = "콘텐츠", example = "이용약관 내용..")
        String content
    ) {}
}
