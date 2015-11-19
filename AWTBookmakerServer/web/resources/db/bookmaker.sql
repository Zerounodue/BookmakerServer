-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Nov 19, 2015 at 10:48 PM
-- Server version: 10.1.8-MariaDB
-- PHP Version: 5.6.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bookmaker`
--

-- --------------------------------------------------------

--
-- Table structure for table `bets`
--

CREATE TABLE `bets` (
  `id` int(11) NOT NULL,
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00',
  `userFK` int(11) NOT NULL,
  `resultFK` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bets`
--

INSERT INTO `bets` (`id`, `amount`, `userFK`, `resultFK`) VALUES
(2, '25.00', 1, 1),
(3, '5.00', 4, 1),
(4, '57.00', 3, 1),
(5, '5.00', 4, 2),
(6, '5.00', 4, 3);

-- --------------------------------------------------------

--
-- Table structure for table `matches`
--

CREATE TABLE `matches` (
  `id` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `homeTeamFK` int(11) NOT NULL,
  `awayTeamFK` int(11) NOT NULL,
  `finished` bit(1) NOT NULL DEFAULT b'0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `matches`
--

INSERT INTO `matches` (`id`, `time`, `homeTeamFK`, `awayTeamFK`, `finished`) VALUES
(2, '2015-11-15 11:14:34', 3, 4, b'0'),
(3, '2016-11-15 11:14:34', 1, 4, b'0'),
(4, '2015-09-10 05:33:52', 4, 7, b'0'),
(5, '2014-09-10 05:33:52', 1, 7, b'0'),
(6, '2016-03-09 21:39:06', 5, 1, b'0'),
(7, '2016-01-09 21:39:06', 4, 2, b'0');

-- --------------------------------------------------------

--
-- Table structure for table `results`
--

CREATE TABLE `results` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `oddNumerator` decimal(12,2) NOT NULL DEFAULT '0.00',
  `oddDenominator` decimal(12,2) NOT NULL DEFAULT '0.00',
  `occured` bit(1) NOT NULL DEFAULT b'0',
  `matchFK` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `results`
--

INSERT INTO `results` (`id`, `name`, `oddNumerator`, `oddDenominator`, `occured`, `matchFK`) VALUES
(1, 'Austria 1:0', '5.00', '1.00', b'0', 7),
(2, 'Austria 2:0', '7.00', '2.00', b'1', 7),
(3, 'Austria 0:1', '1.00', '200.00', b'1', 7);

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `id` int(11) NOT NULL,
  `name` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id`, `name`) VALUES
(1, 'bookmaker'),
(2, 'gambler');

-- --------------------------------------------------------

--
-- Table structure for table `teams`
--

CREATE TABLE `teams` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `teams`
--

INSERT INTO `teams` (`id`, `name`) VALUES
(1, 'Switzerland'),
(2, 'England'),
(3, 'Portugal'),
(4, 'Austria'),
(5, 'Spain'),
(6, 'Germany'),
(7, 'France'),
(8, 'Italy');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `balance` decimal(12,2) NOT NULL DEFAULT '0.00',
  `roleFK` int(11) NOT NULL DEFAULT '2'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `password`, `balance`, `roleFK`) VALUES
(1, 'a', 'a', '0.00', 2),
(3, '1', '2', '0.00', 2),
(4, 'b', 'b', '0.00', 1),
(5, 'c', 'c', '0.00', 2),
(6, 'üpoiuzhg', 'kljhh', '0.00', 2),
(7, '112', '211', '0.00', 2),
(8, 'a1234', '2', '0.00', 2),
(9, 'r', 's', '0.00', 2),
(11, '007', '7', '0.00', 2),
(12, 'hoi', 'du', '0.00', 2),
(13, 'a1', 'a', '0.00', 2),
(14, '321', '1', '0.00', 2);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bets`
--
ALTER TABLE `bets`
  ADD PRIMARY KEY (`id`),
  ADD KEY `matchFK` (`resultFK`),
  ADD KEY `userFK` (`userFK`);

--
-- Indexes for table `matches`
--
ALTER TABLE `matches`
  ADD PRIMARY KEY (`id`),
  ADD KEY `homeTeamFK` (`homeTeamFK`),
  ADD KEY `awayTeamFK` (`awayTeamFK`);

--
-- Indexes for table `results`
--
ALTER TABLE `results`
  ADD PRIMARY KEY (`id`),
  ADD KEY `matchFK` (`matchFK`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `teams`
--
ALTER TABLE `teams`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`),
  ADD KEY `roleFK` (`roleFK`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bets`
--
ALTER TABLE `bets`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `matches`
--
ALTER TABLE `matches`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `results`
--
ALTER TABLE `results`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `teams`
--
ALTER TABLE `teams`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `bets`
--
ALTER TABLE `bets`
  ADD CONSTRAINT `bets_ibfk_1` FOREIGN KEY (`userFK`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `bets_ibfk_2` FOREIGN KEY (`resultFK`) REFERENCES `results` (`id`);

--
-- Constraints for table `matches`
--
ALTER TABLE `matches`
  ADD CONSTRAINT `matches_ibfk_1` FOREIGN KEY (`homeTeamFK`) REFERENCES `teams` (`id`),
  ADD CONSTRAINT `matches_ibfk_2` FOREIGN KEY (`awayTeamFK`) REFERENCES `teams` (`id`);

--
-- Constraints for table `results`
--
ALTER TABLE `results`
  ADD CONSTRAINT `results_ibfk_1` FOREIGN KEY (`matchFK`) REFERENCES `matches` (`id`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`roleFK`) REFERENCES `roles` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
