/*
** prototype.h for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Thu Mar 13 23:22:00 2014 Maxime renaud
** Last update Sun May 11 21:04:41 2014 Maxime renaud
*/

#ifndef		PROTOTYPE_H__
# define	PROTOTYPE_H__

t_elf		*init(char*, t_bool, char *);
char		*str_cut(int, int, char*);
t_form		*create_list(t_elf*, t_sym*);
t_form		*create_list64(t_elf*, t_sym_64*);
t_bool		get_symbol_tab_head(t_elf *, int, Elf32_Shdr *);
t_bool		get_symbol_tab_head64(t_elf *, int, Elf64_Shdr*);
void		sort(t_form **, t_form *, t_bool);
void		free_symbol(t_form *);
void		arg_s(t_elf *);
void		arg_s64(t_elf *);
void		print_inside(Elf32_Shdr*, t_elf*, Elf64_Shdr*, unsigned int);
void		print_head(t_elf *, char *);

#endif
