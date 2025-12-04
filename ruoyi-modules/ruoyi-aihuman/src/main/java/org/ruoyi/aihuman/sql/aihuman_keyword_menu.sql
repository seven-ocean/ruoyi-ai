-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996114192550395904, '关键词管理子表', '2000', '1', 'aihumanKeyword', 'aihuman/aihumanKeyword/index', 1, 0, 'C', '0', '0', 'aihuman:aihumanKeyword:list', '#', 103, 1, sysdate(), null, null, '关键词管理子表菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996114192550395905, '关键词管理子表查询', 1996114192550395904, '1',  '#', '', 1, 0, 'F', '0', '0', 'aihuman:aihumanKeyword:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996114192550395906, '关键词管理子表新增', 1996114192550395904, '2',  '#', '', 1, 0, 'F', '0', '0', 'aihuman:aihumanKeyword:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996114192550395907, '关键词管理子表修改', 1996114192550395904, '3',  '#', '', 1, 0, 'F', '0', '0', 'aihuman:aihumanKeyword:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996114192550395908, '关键词管理子表删除', 1996114192550395904, '4',  '#', '', 1, 0, 'F', '0', '0', 'aihuman:aihumanKeyword:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996114192550395909, '关键词管理子表导出', 1996114192550395904, '5',  '#', '', 1, 0, 'F', '0', '0', 'aihuman:aihumanKeyword:export',       '#', 103, 1, sysdate(), null, null, '');
