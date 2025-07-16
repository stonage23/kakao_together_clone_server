package com.kakao.together.domain.entity.content.extend;

import com.kakao.together.domain.entity.content.Content;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.*;

@Entity
@DiscriminatorValue("TEXT")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TextContent extends Content {

    @Lob
    private String value;
}
