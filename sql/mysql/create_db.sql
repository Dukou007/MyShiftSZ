drop database if exists pax_ppm;

create database pax_ppm;

use pax_ppm;

grant all privileges on pax_ppm.* to 'paxppm'@'%' identified by 'PaxPPM_123@hz';
grant select on mysql.proc TO 'paxppm'@'%';

grant all privileges on pax_ppm.* to 'paxppm'@'localhost' identified by 'PaxPPM_123@hz';
grant select on mysql.proc TO 'paxppm'@'localhost';

