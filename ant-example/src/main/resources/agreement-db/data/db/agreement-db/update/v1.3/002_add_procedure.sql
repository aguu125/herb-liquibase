DROP PROCEDURE IF EXISTS `select_user`;;

    CREATE DEFINER=`root`@`%` PROCEDURE `select_user`(
         IN in_id VARCHAR(64)
         )
    BEGIN
         select * from agreement;
         END
    ;;
call select_user(1);;