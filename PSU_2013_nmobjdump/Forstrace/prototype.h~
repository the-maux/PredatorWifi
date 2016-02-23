/*
** prototype.h for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Thu Mar 13 23:22:00 2014 Maxime renaud
** Last update Sun Mar 16 20:42:25 2014 Maxime renaud
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
void		elf_and_os(Elf32_Ehdr *, char *, Elf64_Ehdr *, t_bool);
void		archi(Elf32_Ehdr *, char *[3], char *[9], t_bool);
void		archi64(Elf64_Ehdr *, char *[3], char *[9], t_bool);
char		getype(Elf32_Sym *, t_elf*, int);
char		getype64(Elf64_Sym *, t_elf*, int);

# define	ARCHIVE		8224

#endif
