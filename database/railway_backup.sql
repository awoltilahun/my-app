-- Railway Database Backup
-- Exported: September 16, 2026
-- Tables: tech_tips (12 records), app_links (0 records)

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS=0;

-- --------------------------------------------------------
-- Table: app_links
-- --------------------------------------------------------

DROP TABLE IF EXISTS `app_links`;
CREATE TABLE `app_links` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `playstore_url` varchar(500) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- No app_links data

-- --------------------------------------------------------
-- Table: tech_tips
-- --------------------------------------------------------

DROP TABLE IF EXISTS `tech_tips`;
CREATE TABLE `tech_tips` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text NOT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `tag` varchar(100) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `video_link` varchar(500) DEFAULT NULL,
  `website_url` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tech Tips Data (12 records)

INSERT INTO `tech_tips` (`id`,`created_at`,`description`,`image_url`,`tag`,`title`,`updated_at`,`video_link`,`website_url`) VALUES
(4,'2026-08-31 20:47:26.301669','ነፃ አንቲቫይረስ (Free Antivirus)። ኃይለኛ የቫይረስ ማጽጃ (Virus Cleaner)። ከፍተኛ ደረጃ ያለው የቫይረስ መፈተሻ እና ማስወገጃ (Virus Scanner & Remover)\n\nየቢትዲፌንደርን (Bitdefender) አሸናፊ የሆነ የቫይረስ መከላከያ ጥበቃ ከእርስዎ ጋር ይያዙ!\n\nBitdefender Antivirus Free በጣም ፈጣን የቫይረስ መፈተሻ፣ ማስወገጃ እና ማጽጃ ብቃት ያለው ኃይለኛ የአንቲቫይረስ መተግበሪያ ነው።','https://res.cloudinary.com/blf6qaue/image/upload/v1788540019/pbxs4uau8mfzf1rm2iyr.png','b1','እሄን ምርጥ app በማውረድ የስልክወን ፍጥነት በብዙ እጥፍ ይጨምሩ !!👌👌 Take Bitdefender\'s award-winning antivirus protection with you!','2026-09-04 16:40:28.158049','','https://play.google.com/store/apps/details?id=com.bitdefender.antivirus'),
(5,'2026-09-01 07:36:39.244866','የሞባይል ኢንተርኔት ፓኬጅዎን በቁጠባ በመጠቀም ለረጅም ጊዜ ለማቆየት የሚረዱ ዋና ዋና ዘዴዎችና ምክሮች።','https://res.cloudinary.com/blf6qaue/image/upload/v1788430155/etechpro/s1k3vls1yrfqbpjkqbtg.jpg','B11','የኢንተርኔት ፓኬጅወን ቆይታ በእጥፍ ይጨምሩ!! SAVE YOU\'R INTERNATE PAKAGE AND USE IT FOR LONG TIME','2026-09-03 10:09:19.961969','',''),
(6,'2026-09-01 07:47:06.646716','የስልክዎን ማህደረ ትውስታ (Phone Storage/Memory) በአግባቡ ለመጠቀምና ነጻ ለማድረግ የሚረዱ ዋና ዋና ዘዴዎች።','https://res.cloudinary.com/blf6qaue/image/upload/v1788430072/etechpro/r9fza4jzjm2bhllkmtvi.jpg','c1','የስልክወን ሚሞሪ ያለጭንቀት ይጠቀሙ! To keep your smartphone running fast and prevent space issues','2026-09-03 10:08:00.009134','',''),
(7,'2026-09-01 08:05:34.777740','የስልክዎን ስክሪን ጤንነት እና ጥራት ጠብቀው ለረጅም ጊዜ ለማቆየት የሚረዱ ዋና ዋና ዘዴዎች።','https://res.cloudinary.com/blf6qaue/image/upload/v1788540076/bsgskvecsnaofue9qokg.png','c11','የስልክወን እስክሪን ጤና ጠብቁ! To keep your phone\'s screen healthy, vivid, and scratch-free for years','2026-09-04 16:41:20.370940','',''),
(8,'2026-09-01 08:22:51.117744','የ AccuBattery አፕሊኬሽን ዋና ዋና ጥቅሞች። AccuBattery extends battery lifespan with charge alarm at 80%.','https://res.cloudinary.com/blf6qaue/image/upload/v1788429859/etechpro/apq3r5gcbpexxyrtrpmz.jpg','b22','ለስልካችሁ ባትሪ እሚአስፈልጋችሁ ምርጥ app ! AccuBattery is one of the best battery management applications','2026-09-03 10:04:30.965019','','https://play.google.com/store/apps/details?id=com.digibites.accubattery'),
(10,'2026-09-02 18:42:09.164808','Zoom በዓለም ዙሪያ ለቪዲዮ ስብሰባዎች፣ ለትምህርት እና ለቀጥታ ውይይቶች በሰፊው የሚወደድ ቀልጣፋ የመገናኛ አፕሊኬሽን ነው። Zoom is a reliable and user-friendly video conferencing platform.','https://res.cloudinary.com/blf6qaue/image/upload/v1788429769/etechpro/tsuckdfncrg7zx81bsox.jpg','a9','የራስወ ቲም ጋር እንደፈለጉ ይወያዩ! Zoom is a reliable and user-friendly video conferencing platform','2026-09-03 10:22:01.327743','','https://play.google.com/store/apps/details?id=us.zoom.videomeetings'),
(11,'2026-09-02 19:16:26.471337','Ethiojobs በኢትዮጵያ ውስጥ የስራ እድሎችን ለማግኘት፣ CV ለማስገባት እና ከአሰሪዎች ጋር ለመገናኘት የሚያስችል ዋና የሥራ መፈለጊያ መተግበሪያ ነው። Ethiojobs is a leading job search and recruitment platform in Ethiopia.','https://res.cloudinary.com/blf6qaue/image/upload/v1788540117/r5i5uly98fmfwukazkzr.jpg','a22','CVወን ብቻ በማስገባት ስራ ያግኙ! Ethiojobs በኢትዮጵያ ውስጥ የስራ እድሎችን ለማግኘት የሚያስችል ዋና የሥራ መፈለጊያ','2026-09-04 16:42:01.928845','','https://play.google.com/store/apps/details?id=com.ethiojobs.mobile'),
(12,'2026-09-02 21:06:07.187637','Dereja በኢትዮጵያ ውስጥ የሚገኙ ተመረቂዎችን እና ወጣቶችን ከስራ እድሎች ጋር የሚያገናኝ መተግበሪያ ነው። Dereja is a dedicated career development platform in Ethiopia.','https://res.cloudinary.com/blf6qaue/image/upload/v1788540141/fpicxe26dk9l9fuomo64.jpg','e2','ተመርቃችሁ ስራ ለምትፈልጉ የሚሆን አፕ! Dereja is a dedicated career development and recruitment platform in Ethiopia','2026-09-04 16:42:28.189760','','https://play.google.com/store/apps/details?id=com.daraja.daraja'),
(13,'2026-09-02 22:00:11.321362','የቃና ቲቪ (Kana TV) ተከታታይ ፊልሞችን እና ድራማዎችን በስልክዎ በቀላሉ እና በጥራት የሚከታተሉበት መመሪያ። Enjoying Kana TV dramas and series directly on your smartphone is convenient and flexible.','https://res.cloudinary.com/blf6qaue/image/upload/v1788540170/gaquiffibujgy9ooywuw.jpg','a33','የ ቃና ቲቪ ፊልሞችን በስልክወ ይመልከቱ! Enjoying Kana TV dramas and series directly on your smartphone','2026-09-04 16:42:55.255376','https://youtu.be/5Ck9T15cxro?si=DxREJ4hTzTJFsu4U',''),
(14,'2026-09-03 08:31:10.504230','CapCut አፕሊኬሽን አጠቃቀም። Basic Editing, Audio Tools, Text & Captions, Effects & Transitions, Exporting in HD/4K.','https://res.cloudinary.com/blf6qaue/image/upload/v1788540228/jos327wt5ujuh90bpt8n.png','A11','በዚህ ቪዲዮ ውስጥ የ CapCut አፕሊኬሽንን በመጠቀም እንዴት ፕሮፌሽናል ቪዲዮዎችን ማዘጋጀት እንደምትችሉ ትማራላችሁ!','2026-09-04 16:43:51.848865','https://youtu.be/NL8Zgtu5K5s?si=IzuCZHTIE7SCkRRt',''),
(22,'2026-09-03 09:40:50.991474','Adobe Premiere Pro ሶፍትዌር አጠቃቀም። Workspace Basics, Cutting & Trimming, Audio Editing, Color Grading, Effects & Titles, Export Settings.','https://res.cloudinary.com/blf6qaue/image/upload/v1788430298/etechpro/zmp5zkeqoipacm7t4boo.jpg','','በዚህ ቪዲዮ Adobe Premiere Pro ሶፍትዌር በመጠቀም ፕሮፌሽናል የቪዲዮ አርትኦት መስራት እንደምትችሉ ትማራላችሁ!','2026-09-03 10:16:57.067794','https://youtu.be/wtSGUZj1O_s?si=GGTB3S6WLzZU7fWn',''),
(23,'2026-09-03 10:39:13.712057','የስልክ ሲም ካርድ ሲግናል (Network Signal) ለማጠናከር የሚረዱ ቀላል መፍትሄዎች። Airplane Mode Toggle, Restart, Re-insert SIM, Change Network Mode, Reset Network Settings.','https://res.cloudinary.com/blf6qaue/image/upload/v1788431948/etechpro/m2ookapaua2pszpgzmne.jpg','c123','የስልክዎ ሲም ካርድ ሲግናል ለማጠናከር ቀላል መፍትሄዎች! Simple fixes to boost your phone network signal strength','2026-09-03 10:39:13.712072','','');

SET FOREIGN_KEY_CHECKS=1;
