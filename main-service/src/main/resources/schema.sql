DROP TABLE IF exists events_compilations, requests, events, compilations, locations, categories, users cascade;

CREATE TABLE IF NOT EXISTS USERS (
	id	bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	name varchar(255) NOT NULL,
	email varchar(255) NOT NULL,
	CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS CATEGORIES (
	id	bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	name varchar(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS LOCATIONS (
	id	bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	lat numeric,
	lon numeric
);

CREATE TABLE IF NOT EXISTS COMPILATIONS (
	id	bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	pinned boolean NOT NULL,
	title varchar(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS EVENTS (
	id	bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	annotation varchar(2000) NOT NULL,
	category_id bigint,
	confirmed_requests integer default 0,
	created_on timestamp without time zone,
	description varchar(7000),
	event_date timestamp without time zone,
	initiator_id bigint,
	location_id bigint,
	paid boolean,
	participant_limit integer default 0,
	published_on timestamp without time zone,
	request_moderation boolean,
	state varchar(50),
	title varchar(200),
	views integer default 0,
	constraint fk_event_user foreign key (initiator_id) references users (id),
	constraint fk_event_category foreign key (category_id) references categories (id),
	constraint fk_event_location foreign key (location_id) references locations (id)
);

CREATE TABLE IF NOT EXISTS REQUESTS (
	id	bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	created  timestamp without time zone,
	event_id bigint not null,
	requester_id bigint not null,
	status varchar(20),
	constraint fk_request_event foreign key (event_id) references events (id),
	constraint fk_request_user foreign key (requester_id) references users (id)
);

CREATE TABLE IF NOT EXISTS EVENTS_COMPILATIONS (
	id	bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	event_id bigint not null,
	compillation_id bigint not null,
	constraint fk_ec_event foreign key (event_id) references events (id),
	constraint fk_ec_compillation foreign key (compillation_id) references compilations (id)
);

CREATE TABLE IF NOT EXISTS comments (
		id	bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
		text varchar(255),
		event_id bigint NOT NULL ,
		author_id bigint NOT NULL,
		created timestamp,
		CONSTRAINT fk_comments_event_id FOREIGN KEY (event_id) REFERENCES events (id),
		CONSTRAINT fk_comments_author_id FOREIGN KEY (author_id) REFERENCES users (id)
);