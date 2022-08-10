CREATE OR REPLACE FUNCTION get_sum_of_whole_digits() RETURNS INT8 AS $$
BEGIN
RETURN SUM(whole_digit) AS sum_of_whole_digits FROM source;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_median_of_fractional_digits() RETURNS DOUBLE PRECISION AS $$
BEGIN
RETURN percentile_cont(0.5) WITHIN GROUP (ORDER BY fractional_digit) FROM "source";
END;
$$ LANGUAGE plpgsql;