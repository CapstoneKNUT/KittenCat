package org.zerock.b01.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStore is a Querydsl query type for Store
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStore extends EntityPathBase<Store> {

    private static final long serialVersionUID = -2080319146L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStore store = new QStore("store");

    public final QMember mid;

    public final StringPath p_address = createString("p_address");

    public final StringPath p_call = createString("p_call");

    public final StringPath p_category = createString("p_category");

    public final StringPath p_content = createString("p_content");

    public final StringPath p_image = createString("p_image");

    public final StringPath p_name = createString("p_name");

    public final StringPath p_opentime = createString("p_opentime");

    public final StringPath p_park = createString("p_park");

    public final StringPath p_site = createString("p_site");

    public final NumberPath<Float> p_star = createNumber("p_star", Float.class);

    public final NumberPath<Long> sno = createNumber("sno", Long.class);

    public QStore(String variable) {
        this(Store.class, forVariable(variable), INITS);
    }

    public QStore(Path<? extends Store> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStore(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStore(PathMetadata metadata, PathInits inits) {
        this(Store.class, metadata, inits);
    }

    public QStore(Class<? extends Store> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.mid = inits.isInitialized("mid") ? new QMember(forProperty("mid")) : null;
    }

}

