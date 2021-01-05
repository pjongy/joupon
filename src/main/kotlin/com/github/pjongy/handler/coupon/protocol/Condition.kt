package com.github.pjongy.handler.coupon.protocol

data class Condition(
  val key: String?,
  val value: String?,
  val operator: String?, // NOTE(pjongy): int_gte, int_lte, int_eq, contains, str_eq ...
  val joinType: String?, // NOTE(pjongy): AND, OR
  val conditions: List<Condition>?
)

/*
(1=11 and 2=22) or (3=33 and 4=44) can be represented:
{
    'conditions': [
        {
            'conditions': [
                {'key': '1', 'value': '11', 'operator': 'int_eq'},
                {'key': '2', 'value': '22', 'operator': 'int_lte'}
            ],
            'join_type': 'AND'
        },
        {
            'conditions': [
                {'key': '3', 'value': '33', 'operator': 'str_eq'},
                {'key': '4', 'value': '44', 'operator': 'int_gte'}
            ],
            'join_type': 'AND'
        }
    ],
    'join_type': 'OR'
}
 */
