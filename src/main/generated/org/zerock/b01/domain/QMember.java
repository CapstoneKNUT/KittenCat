package org.zerock.b01.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -251086651L;

    public static final QMember member = new QMember("member1");

    public final StringPath m_address = createString("m_address");

    public final StringPath m_birth = createString("m_birth");

    public final BooleanPath m_del = createBoolean("m_del");

    public final StringPath m_email = createString("m_email");

    public final StringPath m_gender = createString("m_gender");

    public final StringPath m_mbti = createString("m_mbti");

    public final StringPath m_name = createString("m_name");

    public final StringPath m_phone = createString("m_phone");

    public final StringPath m_pw = createString("m_pw");

    public final BooleanPath m_social = createBoolean("m_social");

    public final StringPath mid = createString("mid");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

