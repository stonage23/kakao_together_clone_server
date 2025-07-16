package com.kakao.together.domain.entity.content.extend;

import com.kakao.together.domain.entity.content.Content;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SUBTITLE")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SubTitleContent extends Content {
    private String value;
}
