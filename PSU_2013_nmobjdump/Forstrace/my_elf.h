/*
** my_elf.h for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Thu Mar 13 23:18:02 2014 Maxime renaud
** Last update Thu Mar 13 23:20:36 2014 Maxime renaud
*/

#ifndef		MY_ELF_H__
# define	MY_ELF_H__

# include	<elf.h>

typedef	enum	e_bool
{
  true, false
}		t_bool;

typedef	struct	s_sym
{
  Elf32_Sym	*symbol;
  char		*string;
  unsigned int	ecx;
}		t_sym;

typedef	struct	s_sym64
{
  Elf64_Sym	*symbol;
  char		*string;
  unsigned int	ecx;
}		t_sym_64;

typedef struct	s_form
{
  Elf32_Addr	addr;
  Elf64_Addr	addr64;
  char		type;
  char		*name;
  struct s_form	*next;
}		t_form;

typedef struct s_elf
{
  void		*maddr;
  unsigned int	mlen;
  t_bool	bit;
  Elf32_Ehdr	*ehdr;
  Elf32_Shdr	*shdr;
  Elf64_Ehdr	*ehdr_64;
  Elf64_Shdr	*shdr_64;
  char		*shstrtab;
  t_sym		sym_stat;
  t_sym		sym_dyna;
  t_sym_64	sym_stat64;
  t_sym_64	sym_dyna64;
}		t_elf;

# include	"prototype.h"

#endif
