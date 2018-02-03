


CREATE SCHEMA `bulkupload` ;

CREATE TABLE `accumulative_deals` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `count` decimal(19,2) DEFAULT NULL,
  `ordering_currency` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `invalid_deal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deal_amount` varchar(255) DEFAULT NULL,
  `date` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `from_currency_iso_code` varchar(255) DEFAULT NULL,
  `to_currency_iso_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8


CREATE TABLE `valid_deal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deal_amount` decimal(19,2) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `from_currency_iso_code` varchar(255) DEFAULT NULL,
  `to_currency_iso_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8