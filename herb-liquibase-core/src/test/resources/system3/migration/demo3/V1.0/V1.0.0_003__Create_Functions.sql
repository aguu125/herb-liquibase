-- ----------------------------
-- nextseq
-- ----------------------------
CREATE FUNCTION `nextseq`(v_seq_name VARCHAR(50))
    RETURNS varchar(50)
begin
    declare i_current_value int;

    set i_current_value = 0;

    return i_current_value;
end;
