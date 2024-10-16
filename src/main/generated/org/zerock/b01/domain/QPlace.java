package org.zerock.b01.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlace is a Querydsl query type for Place
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlace extends EntityPathBase<Place> {

    private static final long serialVersionUID = -2083341956L;

    public static final QPlace place = new QPlace("place");

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

    public final NumberPath<Integer> pord = createNumber("pord", Integer.class);

    public QPlace(String variable) {
        super(Place.class, forVariable(variable));
    }

    public QPlace(Path<? extends Place> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlace(PathMetadata metadata) {
        super(Place.class, metadata);
    }

}

