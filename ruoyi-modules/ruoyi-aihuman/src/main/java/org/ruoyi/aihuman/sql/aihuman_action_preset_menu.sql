-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996052029500645376, '关键词管理', '2000', '1', 'aihumanActionPreset', 'aihuman/aihumanActionPreset/index', 1, 0, 'C', '0', '0', 'aihuman:aihumanActionPreset:list', '#', 103, 1, sysdate(), null, null, '关键词管理菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996052029500645377, '关键词管理查询', 1996052029500645376, '1',  '#', '', 1, 0, 'F', '0', '0', 'aihuman:aihumanActionPreset:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996052029500645378, '关键词管理新增', 1996052029500645376, '2',  '#', '', 1, 0, 'F', '0', '0', 'aihuman:aihumanActionPreset:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996052029500645379, '关键词管理修改', 1996052029500645376, '3',  '#', '', 1, 0, 'F', '0', '0', 'aihuman:aihumanActionPreset:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996052029500645380, '关键词管理删除', 1996052029500645376, '4',  '#', '', 1, 0, 'F', '0', '0', 'aihuman:aihumanActionPreset:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1996052029500645381, '关键词管理导出', 1996052029500645376, '5',  '#', '', 1, 0, 'F', '0', '0', 'aihuman:aihumanActionPreset:export',       '#', 103, 1, sysdate(), null, null, '');
