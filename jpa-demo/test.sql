select order0_.order_number as order_nu1_1_,
       order0_.state        as state2_1_,
       order0_.version      as version3_1_
from purchase_order order0_
where order0_.order_number = ?
