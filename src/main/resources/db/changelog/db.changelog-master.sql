--liquibase formatted sql

--changeset cinema-system:create-genre-table
-- rollback DROP TABLE IF EXISTS genre;
CREATE TABLE IF NOT EXISTS genre
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(512) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

--changeset cinema-system:create-movie-table
-- rollback DROP INDEX IF EXISTS idx_movie_release_date;
-- rollback DROP INDEX IF EXISTS idx_movie_title;
-- rollback DROP TABLE IF EXISTS movie;
CREATE TABLE IF NOT EXISTS movie
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    title        VARCHAR(100) NOT NULL,
    description  VARCHAR(512) NOT NULL,
    release_date DATE         NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_movie_title ON movie(title);
CREATE INDEX IF NOT EXISTS idx_movie_release_date ON movie(release_date);

--changeset cinema-system:create-actor-table
-- rollback DROP TABLE IF EXISTS actor;
CREATE TABLE IF NOT EXISTS actor
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name         VARCHAR(100) NOT NULL,
    birthday   DATE         NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255)
);

--changeset cinema-system:create-director-table
-- rollback DROP TABLE IF EXISTS director;
CREATE TABLE IF NOT EXISTS director
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name         VARCHAR(100) NOT NULL,
    birthday   DATE         NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255)
);

--changeset cinema-system:create-movie_genre-table
-- rollback DROP INDEX IF EXISTS idx_movie_genre_genre_id;
-- rollback DROP TABLE IF EXISTS movie_genre;
CREATE TABLE IF NOT EXISTS movie_genre
(
    movie_id UUID NOT NULL REFERENCES movie(id) ON DELETE CASCADE,
    genre_id UUID NOT NULL REFERENCES genre(id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, genre_id)
);

CREATE INDEX IF NOT EXISTS idx_movie_genre_genre_id ON movie_genre(genre_id);

--changeset cinema-system:create-movie_actor-table
-- rollback DROP INDEX IF EXISTS idx_movie_actor_actor_id;
-- rollback DROP TABLE IF EXISTS movie_actor;
CREATE TABLE IF NOT EXISTS movie_actor
(
    movie_id UUID NOT NULL REFERENCES movie(id) ON DELETE CASCADE,
    actor_id UUID NOT NULL REFERENCES actor(id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, actor_id)
);

CREATE INDEX IF NOT EXISTS idx_movie_actor_actor_id ON movie_actor(actor_id);

--changeset cinema-system:create-director_movie-table
-- rollback DROP INDEX IF EXISTS idx_director_movie_director_id;
-- rollback DROP TABLE IF EXISTS director_movie;
CREATE TABLE IF NOT EXISTS director_movie
(
    movie_id    UUID NOT NULL REFERENCES movie(id) ON DELETE CASCADE,
    director_id UUID NOT NULL REFERENCES director(id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, director_id)
);

CREATE INDEX IF NOT EXISTS idx_director_movie_director_id ON director_movie(director_id);


--changeset cinema-system:add-primary-image-columns
-- rollback ALTER TABLE genre DROP COLUMN image_url;
-- rollback ALTER TABLE director DROP COLUMN profile_picture_url;
-- rollback ALTER TABLE actor DROP COLUMN profile_picture_url;
-- rollback ALTER TABLE movie DROP COLUMN backdrop_url;
-- rollback ALTER TABLE movie DROP COLUMN poster_url;

ALTER TABLE movie
    ADD COLUMN poster_url VARCHAR(1024),
    ADD COLUMN backdrop_url VARCHAR(1024);

ALTER TABLE actor
    ADD COLUMN profile_picture_url VARCHAR(1024);

ALTER TABLE director
    ADD COLUMN profile_picture_url VARCHAR(1024);

ALTER TABLE genre
    ADD COLUMN image_url VARCHAR(1024);


--changeset cinema-system:create-movie_media-table
-- rollback DROP INDEX IF EXISTS idx_movie_media_movie_id;
-- rollback DROP TABLE IF EXISTS movie_media;

CREATE TABLE IF NOT EXISTS movie_media
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    movie_id      UUID         NOT NULL REFERENCES movie (id) ON DELETE CASCADE,
    media_type    VARCHAR(50)  NOT NULL,
    media_url     VARCHAR(1024) NOT NULL,
    title         VARCHAR(255),
    display_order INT          DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_movie_media_movie_id ON movie_media(movie_id);