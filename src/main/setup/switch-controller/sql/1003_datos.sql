---------------------------------------------    
-- ref_profiles
---------------------------------------------
insert into profiles (id, description) values (0, 'Developer');
insert into profiles (id, description) values (1, 'Technical Administrator');
insert into profiles (id, description) values (2, 'Operator');
insert into profiles (id, description) values (3, 'End user');

---------------------------------------------    
-- rights
---------------------------------------------
insert into rights (id, code, description) values (1, 'login','Login');
insert into rights (id, code, description) values (2, 'configuration','Configuration');
insert into rights (id, code, description) values (3, 'configuration.users','Users');
insert into rights (id, code, description) values (4, 'configuration.users.add','Add user');
insert into rights (id, code, description) values (5, 'configuration.users.modify','Modify user');
insert into rights (id, code, description) values (6, 'configuration.users.delete','Delete user');

insert into rights (id, code, description) values (7, 'configuration.parameters','Parameters');
insert into rights (id, code, description) values (8, 'configuration.parameters.modify','Modify parameter');

insert into rights (id, code, description) values (9, 'configuration.profiles','Profiles');
insert into rights (id, code, description) values (10, 'configuration.profiles.modify','Modify profile');

insert into rights (id, code, description) values (11, 'configuration.tasks','Tasks');
insert into rights (id, code, description) values (12, 'configuration.tasks.add','Add task');
insert into rights (id, code, description) values (13, 'configuration.tasks.modify','Modify task');
insert into rights (id, code, description) values (14, 'configuration.tasks.delete','Delete task');

insert into rights (id, code, description) values (15, 'main.log','Show system log');

insert into rights (id, code, description) values (16, 'configuration.interruptors','Tasks');
insert into rights (id, code, description) values (17, 'configuration.interruptors.add','Add interruptor');
insert into rights (id, code, description) values (18, 'configuration.interruptors.modify','Modify interruptor');
insert into rights (id, code, description) values (19, 'configuration.interruptors.delete','Delete interruptor');

insert into profiles_rights (ref_profile, ref_right) values (0, 1);
insert into profiles_rights (ref_profile, ref_right) values (0, 2);
insert into profiles_rights (ref_profile, ref_right) values (0, 3);
insert into profiles_rights (ref_profile, ref_right) values (0, 4);
insert into profiles_rights (ref_profile, ref_right) values (0, 5);
insert into profiles_rights (ref_profile, ref_right) values (0, 6);
insert into profiles_rights (ref_profile, ref_right) values (0, 7);
insert into profiles_rights (ref_profile, ref_right) values (0, 8);
insert into profiles_rights (ref_profile, ref_right) values (0, 9);
insert into profiles_rights (ref_profile, ref_right) values (0, 10);
insert into profiles_rights (ref_profile, ref_right) values (0, 11);
insert into profiles_rights (ref_profile, ref_right) values (0, 12);
insert into profiles_rights (ref_profile, ref_right) values (0, 13);
insert into profiles_rights (ref_profile, ref_right) values (0, 14);
insert into profiles_rights (ref_profile, ref_right) values (0, 15);

insert into profiles_rights (ref_profile, ref_right) values (1, 1);
insert into profiles_rights (ref_profile, ref_right) values (1, 2);
insert into profiles_rights (ref_profile, ref_right) values (1, 3);
insert into profiles_rights (ref_profile, ref_right) values (1, 4);
insert into profiles_rights (ref_profile, ref_right) values (1, 5);
insert into profiles_rights (ref_profile, ref_right) values (1, 6);

---------------------------------------------    
-- users 
---------------------------------------------
insert into users (id, name, login, email, pwd, status, changed, retries, ref_profile, tester) values (1,'Global Administrator', 'root', 'direccion@pryades.com', '94D8244EB7C67002098205AA11F497FB', 0, 20140609, 0, 0, 1 );
insert into users (id, name, login, email, pwd, status, changed, retries, ref_profile, tester) values (2,'Dismer Ronda Betancourt','dismer','dismer.ronda@pryades.com','94D8244EB7C67002098205AA11F497FB',0,20150225,0, 0, 1);

---------------------------------------------    
-- parameters 
---------------------------------------------
insert into parameters (id, description, value, display_order) values (1, 'Default page size in tables', '25', 1);

insert into parameters (id, description, value, display_order) values (2, 'Login fails new password', '10', 11);
insert into parameters (id, description, value, display_order) values (3, 'Login fails block account', '20', 12);
insert into parameters (id, description, value, display_order) values (4, 'Password min size', '8', 13);
insert into parameters (id, description, value, display_order) values (5, 'Password valid time (days)', '365', 14);
insert into parameters (id, description, value, display_order) values (6, 'Mail host address', '', 21);
insert into parameters (id, description, value, display_order) values (7, 'Mail sender email', '', 22);
insert into parameters (id, description, value, display_order) values (8, 'Mail sender user', '', 23);
insert into parameters (id, description, value, display_order) values (9, 'Mail sender password', '', 24);

insert into parameters (id, description, value, display_order) values (10, 'Proxy host for outgoing connections', '', 31);
insert into parameters (id, description, value, display_order) values (11, 'Proxy port for outgoing connections', '', 32);

insert into parameters (id, description, value, display_order) values (12, 'SOCKS5 Proxy host for outgoing connections', '', 41);
insert into parameters (id, description, value, display_order) values (13, 'SOCKS5 Proxy port for outgoing connections', '', 42);

insert into parameters (id, description, value, display_order) values (14, 'Enable password min size', '1', 51);
insert into parameters (id, description, value, display_order) values (15, 'Enable password must contains uppercase', '1', 52);
insert into parameters (id, description, value, display_order) values (16, 'Enable password must contains digit', '1', 53);
insert into parameters (id, description, value, display_order) values (17, 'Enable password must contains symbol', '1', 54);
insert into parameters (id, description, value, display_order) values (18, 'Enable password forbid contains id', '1', 55);
insert into parameters (id, description, value, display_order) values (19, 'Enable password forbid reuse passwords', '1', 56);

insert into parameters (id, description, value, display_order) values (20, 'Charts export image width', '640', 61);
insert into parameters (id, description, value, display_order) values (21, 'Charts export image height', '480', 62);
insert into parameters (id, description, value, display_order) values (22, 'Max number of rows to export', '1000', 63);

insert into parameters (id, description, value, display_order) values (23, 'Log default', 'E', 71);
