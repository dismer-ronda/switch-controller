CREATE SEQUENCE gendat START 1000000;
CREATE SEQUENCE gencfg START 50000;

-- plattform parameters
create table parameters (
	id bigint not null,
    
	description varchar(256) not null,					-- name of the parameter	
    value varchar(256) not null,						-- value of the parameter
	display_order integer not null default(0),
	
	constraint pk_parameters primary key(id),
	constraint uk_parameters unique(description)
);
create index ix_parameters_description on parameters(description);

-- user profiles
create table profiles (
	id bigint not null,
	description varchar(128) not null,
	
	constraint pk_profiles primary key(id),
	constraint uk_profiles_description unique (description)	
);
	
-- user rights
create table rights (
	id bigint not null,
	code varchar(128),
	description varchar(128) not null,
	
	constraint pk_rights primary key(id),
	constraint uk_rights_code unique (code)	
);
	
create table profiles_rights (
    ref_profile bigint not null,
    ref_right bigint not null,
    
    constraint pk_profiles_rights primary key(ref_profile, ref_right),
    
    constraint fk_profiles_rights_profile foreign key (ref_profile) references profiles(id),
    constraint fk_profiles_rights_right foreign key (ref_right) references rights(id)		
    );

-- users
create table users (
    id bigint not null,
    
    name varchar(128) not null,   			-- user name
    login varchar(64) not null,    			-- user login
    email varchar(128) not null,    		-- user email
	pwd varchar(32) not null,       		-- password hash

	status integer not null,        		-- 0=logged -- 1=Never logged -- 2=Request change -- 3=password expired -- 4=too manu retries
	changed integer not null,        		-- password changed date
	retries integer not null,      			-- number of retries failed
	tester integer not null default 0,		-- 1: user has access to pre-release features, 0: user has access only to released features

   	ref_profile bigint not null,   			-- user's profile

    constraint pk_users primary key(id),
    constraint fk_users_profile foreign key (ref_profile) references profiles(id),
	constraint uk_users_code unique(login),
	constraint uk_users_email unique(email)
    );

-- user_defaults
create table user_defaults (
	id bigint not null,
    
	data_key varchar(64) not null,		-- key for data	
	data_value varchar(64),				-- value for data	
	
	ref_user bigint not null,			-- user	

	constraint pk_user_defaults primary key(id),
	constraint uk_user_defaults_key unique(ref_user, data_key),
	constraint fk_user_defaults_user foreign key (ref_user) references users(id)
);

-- tasks
create table tasks (
	id bigint not null,

	description varchar (64) not null,			-- description of the task

	timezone varchar(128) not null,
	language varchar(16) not null,
	month varchar(64),							-- month to create the report (* for all months)
	day varchar(64),							-- day to create the report (* for all days, number for specific day or MON, TUE, WED for week days)
	hour varchar(64),							-- hour to create the report (* for all hours)
	times integer not null, 					-- number of times task is executed: 0 for infinite number 
	
	clazz integer not null,						-- class of report
	details text,								-- text with report details
	
	system integer not null, 					-- system task 

	constraint pk_tasks primary key(id)
);
create index ix_tasks_clazz on tasks(clazz);
create index ix_tasks_system on tasks(system);

-- free days     
create table free_days (
    id integer not null,
    name varchar(32) not null, 
	thedate integer not null,
    constraint pk_free_days primary key(id)
    );

-- interruptors
create table interruptors (
    id integer not null,
    list_order integer not null,	
	name varchar(32) not null,   	
	description varchar(128),   	
	address varchar(32),   
	power float not null,			

	plan_labor bytea,									
    plan_free bytea,								

    enabled integer not null default 1,
    state integer,
    last_signal bigint,

    constraint pk_interruptors primary key(id),
    constraint uk_interruptors_name unique (name)    
    );

-- instalaciones
create table facilities (
    id integer not null,
    list_order integer not null,
	name varchar(32) not null,
	description varchar(128),
	power float not null,
	
    enabled integer not null default 1,

    constraint pk_facilities primary key(id),
    constraint uk_facilities_name unique (name)    
    );

create table facility_interruptors (
    ref_facility bigint not null,
    ref_interruptor bigint not null,
    
    constraint pk_facility_interruptors primary key(ref_facility, ref_interruptor),
    
    constraint fk_facility_interruptors_facility foreign key (ref_facility) references facilities(id) on delete cascade,
    constraint fk_facility_interruptors_interruptor foreign key (ref_interruptor) references interruptors(id) on delete cascade
    );

-- holidays
create table holidays (
    id integer not null,
	name varchar(64) not null,   	
    from_date integer,
    to_date integer,

    constraint pk_holidays primary key(id),
    constraint uk_holidays_name unique (name)    
    );

create table facility_holidays (
    ref_facility bigint not null,
    ref_holiday bigint not null,
    
    constraint pk_facility_holidays primary key(ref_facility, ref_holiday),
    
    constraint fk_facility_holidays_facility foreign key (ref_facility) references facilities(id) on delete cascade,
    constraint fk_facility_holidays_holiday foreign key (ref_holiday) references holidays(id) on delete cascade
    );


